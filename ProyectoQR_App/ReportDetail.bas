B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.1
@EndOfDesignText@
' ReportDetail.bas
' Activity que muestra el contenido de un reporte (area + car_reports)
' Versión robusta: parseo tolerante y normalización automática.
' Mejoras: muestra observaciones/incidencias de forma fiable y traduce booleans a "Correcto"/"No correcto".

#Region  Activity Attributes
    #FullScreen: False
    #IncludeTitle: True
#End Region

Sub Process_Globals
	Public ReportJson As String ' opcional: si se asigna externamente
	Public AllowEdit As Boolean = False
End Sub

Sub Globals
	Private sv As ScrollView
	Private pnl As Panel
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.RemoveAllViews
	Activity.Title = "Detalle reporte"

	sv.Initialize(100%y)
	Activity.AddView(sv, 0, 0, 100%x, 100%y)
	pnl = sv.Panel
	pnl.RemoveAllViews

	LoadAndRenderCurrentReport
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

' -------------------------------------------------------
' Carga y parsea current_report.json (o ReportJson si se asignó)
' Normaliza wrappers y llama a RenderReport(rp As Map)
' -------------------------------------------------------
Sub LoadAndRenderCurrentReport
	Dim jsonToUse As String = ReportJson
	If jsonToUse = "" Then
		Try
			If File.Exists(File.DirInternal, "current_report.json") Then
				jsonToUse = File.ReadString(File.DirInternal, "current_report.json")
			End If
		Catch
			Log("ReportDetail: Error reading current_report.json: " & LastException.Message)
		End Try
	End If

	If jsonToUse = Null Then jsonToUse = ""
	jsonToUse = jsonToUse.Trim
	If jsonToUse = "" Then
		Dim lbl As Label
		lbl.Initialize("lbl_empty")
		lbl.Text = "No hay datos para mostrar."
		lbl.TextSize = 16
		pnl.AddView(lbl, 10dip, 10dip, 100%x - 20dip, EstimateLabelHeight(lbl.Text, lbl.TextSize, 100%x - 20dip))
		Return
	End If

	' quitar BOM invisibles
	Try
		jsonToUse = jsonToUse.Replace(Chr(65279), "")
	Catch
		Log("ReportDetail: BOM replace error: " & LastException.Message)
	End Try

	' Extraer primer bloque JSON completo
	Dim extracted As String = ExtractFirstJson(jsonToUse)
	If extracted = "" Then
		Log("ReportDetail: no se encontró bloque JSON completo. Snippet: " & jsonToUse.SubString2(0, Min(jsonToUse.Length, 800)))
		ToastMessageShow("Formato de reporte inválido (ver logs).", True)
		Return
	End If

	' Limpiar caracteres de control < 32 (excepto tab, LF, CR)
	Dim sb As StringBuilder
	sb.Initialize
	For i = 0 To extracted.Length - 1
		Dim ch As String = extracted.SubString2(i, i + 1)
		Dim b() As Byte = ch.GetBytes("UTF8")
		Dim code As Int = 32
		If b.Length > 0 Then
			code = b(0)
			If code < 0 Then code = code + 256
		End If
		If code >= 32 Or code = 9 Or code = 10 Or code = 13 Then
			sb.Append(ch)
		End If
	Next
	Dim cleaned As String = sb.ToString

	' Parsear UNA vez con NextObject / NextArray
	Dim parser As JSONParser
	Dim root As Object
	Try
		parser.Initialize(cleaned)
		If cleaned.StartsWith("{") Then
			root = parser.NextObject
			Log("ReportDetail: Parsed cleaned as JSON Object")
		Else If cleaned.StartsWith("[") Then
			root = parser.NextArray
			Log("ReportDetail: Parsed cleaned as JSON Array")
		Else
			Log("ReportDetail: cleaned no empieza con { ni [")
			root = Null
		End If
	Catch
		Log("ReportDetail: parse error: " & LastException.Message)
		root = Null
	End Try

	If root = Null Then
		Try
			Dim snippet As String = cleaned
			If snippet.Length > 2000 Then snippet = snippet.SubString2(0, 2000)
			Log("ReportDetail: FINAL_PARSE_FAILED_SNIPPET: " & snippet)
		Catch
			Log(LastException.Message)
		End Try
		ToastMessageShow("No se pudo parsear detalle (ver Logcat).", True)
		Return
	End If

	' Normalizar root a Map (si viene array tomar el primer elemento Map)
	Dim rp As Map
	rp = Null
	If root Is Map Then
		rp = root
	Else If root Is List Then
		Dim lst As List = root
		If lst.Size > 0 And lst.Get(0) Is Map Then
			rp = lst.Get(0)
		Else
			Log("ReportDetail: JSON lista sin objetos Map.")
			ToastMessageShow("Formato de reporte inesperado.", True)
			Return
		End If
	Else
		Log("ReportDetail: root no es Map ni List.")
		ToastMessageShow("Formato de reporte inesperado.", True)
		Return
	End If

	' Si rp viene envuelto en clave 'parsed', desempaquetar
	Try
		If rp.ContainsKey("parsed") Then
			Dim maybeParsed As Object = rp.Get("parsed")
			If maybeParsed Is Map Then rp = maybeParsed
		End If
	Catch
		Log(LastException.Message)
	End Try

	' Si rp no tiene 'area' pero tiene JSON_Reporte (string), intentar parsearlo
	Try
		If rp.ContainsKey("area") = False And rp.ContainsKey("JSON_Reporte") Then
			Dim sj As String = rp.Get("JSON_Reporte")
			If sj <> "" Then
				Dim p2 As JSONParser
				p2.Initialize(sj)
				Dim inner As Object
				Dim sjTrim As String = sj.Trim
				If sjTrim.StartsWith("{") Then
					inner = p2.NextObject
				Else If sjTrim.StartsWith("[") Then
					inner = p2.NextArray
				Else
					inner = p2.NextValue
				End If
				If inner Is Map Then rp = inner
			End If
		End If
	Catch
		Log("ReportDetail: no se pudo parsear rp.JSON_Reporte interno: " & LastException.Message)
	End Try

	' Finalmente, renderizar con tu rutina existente
	Try
		RenderReport(rp)
	Catch
		Log("ReportDetail: error en RenderReport: " & LastException.Message)
		ToastMessageShow("No se pudo mostrar el reporte (ver logs).", True)
	End Try
End Sub

' -------------------------------------------------------
' Extrae el primer bloque JSON completo (objeto o array) de una cadena.
' Maneja comillas y escapes para no confundir llaves dentro de strings.
' -------------------------------------------------------
Sub ExtractFirstJson(s As String) As String
	If s = Null Then Return ""
	s = s.Trim
	Dim len As Int = s.Length
	If len = 0 Then Return ""

	Dim firstObj As Int = s.IndexOf("{")
	Dim firstArr As Int = s.IndexOf("[")
	Dim start As Int
	If firstObj = -1 And firstArr = -1 Then Return ""
	If firstObj = -1 Then
		start = firstArr
	Else If firstArr = -1 Then
		start = firstObj
	Else
		If firstObj < firstArr Then
			start = firstObj
		Else
			start = firstArr
		End If
	End If

	Dim depth As Int = 0
	Dim inString As Boolean = False
	Dim escaped As Boolean = False

	For i = start To len - 1
		Dim ch As String = s.SubString2(i, i + 1)
		If escaped Then
			escaped = False
			Continue
		End If
		If ch = "\" Then
			escaped = True
			Continue
		End If
		If ch = """" Then
			inString = Not(inString)
			Continue
		End If

		If Not(inString) Then
			If ch = "{" Or ch = "[" Then
				depth = depth + 1
			Else If ch = "}" Or ch = "]" Then
				depth = depth - 1
				If depth = 0 Then
					Return s.SubString2(start, i + 1)
				End If
			End If
		End If
	Next

	Return ""
End Sub

' -------------------------------------------------------
' Renderiza el mapa del reporte (mejorado)
' - ahora separa responses y extras en etiquetas distintas
' - traduce booleanos a "Correcto"/"No correcto"
' -------------------------------------------------------
Sub RenderReport(rp As Map)
	Dim y As Int = 10dip
	Dim padding As Int = 10dip
	Dim w As Int = 100%x - 2 * padding

	' Area info
	If rp.ContainsKey("area") Then
		Try
			Dim area As Map = rp.Get("area")
			Dim lblTitle As Label
			lblTitle.Initialize("lblTitle")
			Dim name As String = ""
			If area.ContainsKey("area_name") Then name = area.Get("area_name")
			If name = "" And area.ContainsKey("areaName") Then name = area.Get("areaName")
			lblTitle.Text = "Área: " & name
			lblTitle.TextSize = 16
			lblTitle.Typeface = Typeface.DEFAULT_BOLD
			Dim hTitle As Int = EstimateLabelHeight(lblTitle.Text, lblTitle.TextSize, w)
			pnl.AddView(lblTitle, padding, y, w, hTitle)
			y = y + hTitle + 6dip

			Dim desc As String = ""
			If area.ContainsKey("area_description") Then desc = area.Get("area_description")
			If desc = "" And area.ContainsKey("areaDescription") Then desc = area.Get("areaDescription")
			If desc <> "" Then
				Dim lblDesc As Label
				lblDesc.Initialize("lblDesc")
				lblDesc.Text = "Descripción: " & desc
				lblDesc.TextSize = 14
				Dim hDesc As Int = EstimateLabelHeight(lblDesc.Text, lblDesc.TextSize, w)
				pnl.AddView(lblDesc, padding, y, w, hDesc)
				y = y + hDesc + 6dip
			End If
		Catch
			Log("Error leyendo campo area: " & LastException.Message)
		End Try
	End If

	' CAR reports
	If rp.ContainsKey("car_reports") Then
		Try
			Dim cars As List = rp.Get("car_reports")
			For i = 0 To cars.Size - 1
				Dim c As Map = cars.Get(i)
				Dim carName As String = ""
				If c.ContainsKey("car_name") Then carName = c.Get("car_name")

				' Card panel
				Dim card As Panel
				card.Initialize("card" & i)
				card.Color = Colors.ARGB(255, 245, 245, 245)
				pnl.AddView(card, padding, y, w, 60dip) ' altura provisional; la ajustaremos luego

				' Car name
				Dim lblCar As Label
				lblCar.Initialize("lblCar" & i)
				lblCar.Text = "CAR: " & carName
				lblCar.TextSize = 15
				lblCar.Typeface = Typeface.DEFAULT_BOLD
				Dim hCarName As Int = EstimateLabelHeight(lblCar.Text, lblCar.TextSize, w - 16dip)
				card.AddView(lblCar, 8dip, 4dip, w - 16dip, hCarName)

				' Responses (formatted) - ahora usando FormatValue para traducir booleans
				Dim responsesText As String = ""
				If c.ContainsKey("responses") Then
					Try
						Dim respMap As Map = c.Get("responses")
						For Each k As String In respMap.Keys
							Try
								Dim v As Object = respMap.Get(k)
								Dim vs As String = FormatValue(v)
								responsesText = responsesText & k & ": " & vs & CRLF
							Catch
								Log("RenderReport: error leyendo responses key '" & k & "': " & LastException.Message)
							End Try
						Next
					Catch
						responsesText = "Error leyendo responses"
						Log("Error reading responses map: " & LastException.Message)
					End Try
				End If

				' Observacion / incidencia (busca variantes y serializa si hace falta) -> almacena en extrasText
				Dim extrasText As String = ""

				' helpers: listas de variantes de claves
				Dim obsKeys As List
				obsKeys.Initialize
				obsKeys.AddAll(Array As String("observacion","observación","Observacion","Observación","observaciones","Observaciones","obs"))

				Dim incKeys As List
				incKeys.Initialize
				incKeys.AddAll(Array As String("incidencia","Incidencia","incidencias","Incidencias","incid","incidencias_text"))

				' buscar observación
				For Each k As String In obsKeys
					If c.ContainsKey(k) Then
						Try
							Dim vv As Object = c.Get(k)
							Dim sVal As String = FormatValue(vv)
							If sVal <> "" Then
								extrasText = extrasText & "Observación: " & sVal & CRLF
								Log("RenderReport: found observacion key '" & k & "': " & sVal.SubString2(0, Min(200, sVal.Length)))
								Exit
							End If
						Catch
							Log("RenderReport: error leyendo observacion key '" & k & "': " & LastException.Message)
						End Try
					End If
				Next

				' buscar incidencia
				For Each k As String In incKeys
					If c.ContainsKey(k) Then
						Try
							Dim vv2 As Object = c.Get(k)
							Dim sVal2 As String = FormatValue(vv2)
							If sVal2 <> "" Then
								extrasText = extrasText & "Incidencia: " & sVal2 & CRLF
								Log("RenderReport: found incidencia key '" & k & "': " & sVal2.SubString2(0, Min(200, sVal2.Length)))
								Exit
							End If
						Catch
							Log("RenderReport: error leyendo incidencia key '" & k & "': " & LastException.Message)
						End Try
					End If
				Next

				' Si no encontramos nada, buscar en responses (a veces viene ahí)
				If extrasText = "" And c.ContainsKey("responses") Then
					Try
						Dim respMap2 As Map = c.Get("responses")
						' check same obs/inc keys inside responses
						For Each k As String In obsKeys
							If respMap2.ContainsKey(k) Then
								Try
									Dim vv As Object = respMap2.Get(k)
									Dim sVal As String = FormatValue(vv)
									extrasText = extrasText & "Observación: " & sVal & CRLF
									Log("RenderReport: found observacion inside responses key '" & k & "': " & sVal.SubString2(0, Min(200, sVal.Length)))
									Exit
								Catch
									Log("RenderReport: error leyendo observacion inside responses key '" & k & "': " & LastException.Message)
								End Try
							End If
						Next

						If extrasText = "" Then
							For Each k As String In incKeys
								If respMap2.ContainsKey(k) Then
									Try
										Dim vv4 As Object = respMap2.Get(k)
										Dim sVal2 As String = FormatValue(vv4)
										extrasText = extrasText & "Incidencia: " & sVal2 & CRLF
										Log("RenderReport: found incidencia inside responses key '" & k & "': " & sVal2.SubString2(0, Min(200, sVal2.Length)))
										Exit
									Catch
										Log("RenderReport: error leyendo incidencia inside responses key '" & k & "': " & LastException.Message)
									End Try
								End If
							Next
						End If
					Catch
						Log("RenderReport: error buscando observ/incid en responses: " & LastException.Message)
					End Try
				End If

				' Ahora renderizamos: primero responses, luego extras (cada uno en su Label separado)
				Dim detailsResp As String = responsesText
				Dim detailsExtras As String = extrasText

				If detailsResp = "" Then detailsResp = "(sin respuestas)"
				If detailsExtras = "" Then detailsExtras = "" ' si no hay extras no mostramos etiqueta extra

				' Label responses
				Dim lblResp As Label
				lblResp.Initialize("lblResp" & i)
				lblResp.Text = detailsResp
				lblResp.TextSize = 13
				Dim hResp As Int = EstimateLabelHeight(lblResp.Text, lblResp.TextSize, w - 16dip)
				card.AddView(lblResp, 8dip, 4dip + hCarName, w - 16dip, hResp)

				Dim hExtras As Int = 0
				If detailsExtras <> "" Then
					Dim lblExtras As Label
					lblExtras.Initialize("lblExtras" & i)
					lblExtras.Text = detailsExtras
					lblExtras.TextSize = 13
					hExtras = EstimateLabelHeight(lblExtras.Text, lblExtras.TextSize, w - 16dip)
					card.AddView(lblExtras, 8dip, 4dip + hCarName + hResp, w - 16dip, hExtras)
				End If

				' ajustar altura del card según contenido
				Dim hCard As Int = hCarName + hResp + hExtras + 16dip
				card.SetLayout(card.Left, card.Top, card.Width, hCard)
				y = y + hCard + 8dip
			Next
		Catch
			Log("Error leyendo car_reports: " & LastException.Message)
		End Try
	Else
		' Si no hay car_reports, intentar mostrar 'raw' si existe
		If rp.ContainsKey("raw") Then
			Try
				Dim rawObj As Object = rp.Get("raw")
				Dim rawText As String = ""
				If rawObj Is Map Then
					Dim jg As JSONGenerator
					jg.Initialize(rawObj)
					rawText = jg.ToString
				Else
					rawText = rawObj
				End If
				Dim lblRaw As Label
				lblRaw.Initialize("lblRaw")
				lblRaw.Text = rawText
				lblRaw.TextSize = 13
				Dim hRaw As Int = EstimateLabelHeight(lblRaw.Text, lblRaw.TextSize, w)
				pnl.AddView(lblRaw, 10dip, y, w, hRaw)
				y = y + hRaw + 6dip
			Catch
				Log("Error mostrando raw: " & LastException.Message)
			End Try
		End If
	End If

	pnl.Height = y + 20dip
	
	' Agregar botones de acción al final SOLO si está permitido
	If AllowEdit Then
		AddActionButtons(y + 30dip)
	End If
End Sub

Sub AddActionButtons(top As Int)
	Dim btnEdit As Button
	btnEdit.Initialize("btnEdit")
	btnEdit.Text = "Editar Reporte"
	btnEdit.Color = Colors.RGB(255, 193, 7) ' Amber/Orange
	btnEdit.TextColor = Colors.Black
	pnl.AddView(btnEdit, 10dip, top, 45%x, 50dip)

	Dim btnDelete As Button
	btnDelete.Initialize("btnDelete")
	btnDelete.Text = "Eliminar"
	btnDelete.Color = Colors.RGB(220, 53, 69) ' Red
	btnDelete.TextColor = Colors.White
	pnl.AddView(btnDelete, 55%x, top, 40%x, 50dip)

	pnl.Height = top + 70dip
End Sub

Sub btnEdit_Click
	Dim jsonRaw As String = File.ReadString(File.DirInternal, "current_report.json")
	Dim parser As JSONParser
	parser.Initialize(jsonRaw)
	Dim report As Map = parser.NextObject

	' Chequear si tenemos Id_Reporte
	If report.ContainsKey("Id_Reporte") = False Then
		ToastMessageShow("Datos incompletos: falta Id_Reporte.", True)
		Return
	End If
	
	' Chequear si tenemos definición completa de área (con cars)
	Dim hasLocalArea As Boolean = False
	If report.ContainsKey("area") Then
		Dim aMap As Map = report.Get("area")
		If aMap.IsInitialized And aMap.ContainsKey("cars") And IsListValid(aMap.Get("cars")) Then
			hasLocalArea = True
		End If
	End If

	If hasLocalArea Then
		' Todo bien, tenemos definición local, abrir editor
		OpenEditDialog(report)
	Else
		' Falta definición local. Buscar si tenemos un Id_Area para pedirla.
		Dim idAreaToFetch As String = ""
		If report.ContainsKey("Id_Area") Then 
			idAreaToFetch = report.Get("Id_Area")
		Else If report.ContainsKey("area") Then
			Dim partialArea As Map = report.Get("area")
			If partialArea.ContainsKey("Id_Area") Then idAreaToFetch = partialArea.Get("Id_Area")
			If idAreaToFetch = "" And partialArea.ContainsKey("id") Then idAreaToFetch = partialArea.Get("id")
		End If
		
		If idAreaToFetch <> "" Then
			ProgressDialogShow("Obteniendo datos del área para editar...")
			FetchAreaForEdit(idAreaToFetch, report)
		Else
			ToastMessageShow("No se puede editar: falta información de área (Id_Area) y CARs.", True)
		End If
	End If
End Sub

Sub IsListValid(l As Object) As Boolean
	If l Is List Then
		Dim lst As List = l
		Return lst.Size > 0
	End If
	Return False
End Sub

Sub FetchAreaForEdit(idArea As Int, reportMap As Map)
	Dim job As HttpJob
	job.Initialize("GetAreaForEdit", Me)
	' Guardamos el mapa del reporte en la propiedad Tag para recuperarlo en JobDone
	job.Tag = reportMap
	Dim body As String = "id=" & idArea ' getArea.php busca por codigo/id
	job.PostString("https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api/getArea.php", body)
End Sub

Sub OpenEditDialog(report As Map)
	Dim areaMap As Map = report.Get("area")
	' Envolver en data/success como espera ReportDialog (simular respuesta API getArea)
	Dim areaWrapper As Map
	areaWrapper.Initialize
	areaWrapper.Put("success", True)
	areaWrapper.Put("data", areaMap)
	Dim jg As JSONGenerator
	jg.Initialize(areaWrapper)
	File.WriteString(File.DirInternal, "last_area.json", jg.ToString)

	' Configurar ReportDialog
	ReportDialog.IsEditing = True
	ReportDialog.EditReportId = report.Get("Id_Reporte")
	ReportDialog.PreloadedReport = report ' Pasamos todo el mapa (contiene car_reports)

	StartActivity(ReportDialog)
	Activity.Finish
End Sub

Sub btnDelete_Click
	Msgbox2Async("¿Seguro que deseas eliminar este reporte?", "Eliminar", "Sí", "Cancelar", "", Null, True)
	Wait For Msgbox_Result (Result As Int)
	If Result = DialogResponse.POSITIVE Then
		Dim jsonRaw As String = File.ReadString(File.DirInternal, "current_report.json")
		Dim parser As JSONParser
		parser.Initialize(jsonRaw)
		Dim report As Map = parser.NextObject
		
		If report.ContainsKey("Id_Reporte") Then
			Dim id As Int = report.Get("Id_Reporte")
			DeleteReport(id)
		Else
			ToastMessageShow("No se encontró Id_Reporte.", True)
		End If
	End If
End Sub

Sub DeleteReport(id As Int)
	Dim job As HttpJob
	job.Initialize("DeleteJob", Me)
	Dim url As String = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api/delete_report.php"
	Dim json As JSONGenerator
	Dim m As Map
	m.Initialize
	m.Put("Id_Reporte", id)
	json.Initialize(m)
	job.PostString(url, json.ToString)
End Sub

Sub JobDone(Job As HttpJob)
	If Job.JobName = "DeleteJob" Then
		If Job.Success Then
			ToastMessageShow("Reporte eliminado.", True)
			Activity.Finish
		Else
			ToastMessageShow("Error eliminando: " & Job.ErrorMessage, True)
		End If
	Else If Job.JobName = "GetAreaForEdit" Then
		ProgressDialogHide
		If Job.Success Then
			Dim res As String = Job.GetString
			Log("GetAreaForEdit RAW: " & res) ' <--- DEBUG LOG
			
			' Parsear respuesta getArea
			Dim parser As JSONParser
			parser.Initialize(res)
			Try
				Dim root As Map = parser.NextObject
				' Extraer data
				Dim areaData As Map
				If root.ContainsKey("success") And root.Get("success") = True Then
					areaData = root.Get("data")
				Else
					areaData = root
				End If
				
				Log("GetAreaForEdit areaData keys: " & areaData.Keys) ' <--- DEBUG LOG
				
				' Validar si tiene cars
				Dim hasCars As Boolean = False
				If areaData.ContainsKey("cars") Then
					Dim cObj As Object = areaData.Get("cars")
					If IsListValid(cObj) Then 
						hasCars = True
						Log("Found 'cars' direct list. Size: " & AsList(cObj).Size)
					End If
				End If
				
				If hasCars = False And areaData.ContainsKey("JSON_Area") Then
					Log("Checking JSON_Area for cars...")
					Try
						Dim jas As String = areaData.Get("JSON_Area")
						Log("JSON_Area string: " & jas)
						Dim jp As JSONParser
						jp.Initialize(jas)
						Dim fullArea As Map = jp.NextObject
						If fullArea.ContainsKey("cars") Then 
							areaData.Put("cars", fullArea.Get("cars"))
							hasCars = True
							Log("Recovered cars from JSON_Area. List valid? " & IsListValid(areaData.Get("cars")))
						End If
					Catch
						Log("Error parsing JSON_Area for edit: " & LastException.Message)
					End Try
				End If
				
				If hasCars = False Then
					Log("ERROR: No valid cars found in areaData.")
					ToastMessageShow("El área no tiene definición de CARs válida.", True)
				Else
					' Ahora inyectamos esta nueva definición de área en el reporte (solo en memoria)
					Dim originalReport As Map = Job.Tag
					originalReport.Put("area", areaData)
					
					' Y abrimos el editor
					OpenEditDialog(originalReport)
				End If
			Catch
				Log("Error parsing GetAreaForEdit response: " & LastException.Message)
				ToastMessageShow("Error procesando datos del área.", True)
			End Try
		Else
			ToastMessageShow("No se pudo obtener datos del área: " & Job.ErrorMessage, True)
		End If
	End If
	Job.Release
End Sub

' -------------------------------------------------------
' Formatea cualquier valor a String legible:
' - booleanos -> "Correcto"/"No correcto"
' - Map/List -> JSON string
' - null -> ""
' - números/strings -> su representación
' -------------------------------------------------------
Sub FormatValue(v As Object) As String
	If v = Null Then Return ""
	Try
		' booleanos
		If v = True Then Return "Correcto"
		If v = False Then Return "No correcto"

		' Map o List -> serializar
		If v Is Map Or v Is List Then
			Dim jg As JSONGenerator
			jg.Initialize(v)
			Return jg.ToString
		End If

		' por defecto: convertir a string
		Return v
	Catch
		Log("FormatValue error: " & LastException.Message)
		Return ""
	End Try
End Sub

' -------------------------------------------------------
' Estima una altura adecuada (en px/dip) para un Label con texto multilínea.
' -------------------------------------------------------
Sub EstimateLabelHeight(text As String, textSize As Int, width As Int) As Int
	If text = "" Then Return 30dip
	If width <= 0 Then width = 100dip
	Dim charWidth As Int = (textSize * 6) / 10
	If charWidth < 4 Then charWidth = 4
	Dim charsPerLine As Int = width / charWidth
	If charsPerLine < 1 Then charsPerLine = 1
	Dim textLen As Int = text.Length
	Dim lines As Int = textLen / charsPerLine
	If (textLen Mod charsPerLine) <> 0 Then lines = lines + 1
	Dim lineHeight As Int = (textSize * 16) / 10
	If lineHeight < 14 Then lineHeight = 14
	Dim result As Int = lines * lineHeight + 6dip
	Return result
End Sub

Sub AsList(l As Object) As List
	Dim res As List = l
	Return res
End Sub
