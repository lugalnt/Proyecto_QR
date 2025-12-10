B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.1
@EndOfDesignText@
' ReportDetail.bas
' Activity que muestra el contenido de un reporte (area + car_reports)
' Versión robusta: parseo tolerante y normalización automática.

#Region  Activity Attributes
    #FullScreen: False
    #IncludeTitle: True
#End Region

Sub Process_Globals
	Public ReportJson As String ' opcional: si se asigna externamente
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
		' ignorar
	End Try

	' Si rp no tiene 'area' pero tiene JSON_Reporte (string), intentar parsearlo
	Try
		If rp.ContainsKey("area") = False And rp.ContainsKey("JSON_Reporte") Then
			Dim sj As String = rp.Get("JSON_Reporte")
			If sj <> "" Then
				Dim p2 As JSONParser
				p2.Initialize(sj)
				Dim inner As Object = p2.NextValue
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
' Renderiza el mapa del reporte (tu rutina original, con mínimos ajustes)
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

				' Responses (formatted)
				Dim responsesText As String = ""
				If c.ContainsKey("responses") Then
					Try
						Dim respMap As Map = c.Get("responses")
						For Each k As String In respMap.Keys
							Try
								Dim v As Object = respMap.Get(k)
								responsesText = responsesText & k & ": " & v & CRLF
							Catch
								Log("RenderReport: error leyendo responses key '" & k & "': " & LastException.Message)
							End Try
						Next
					Catch
						responsesText = "Error leyendo responses"
						Log("Error reading responses map: " & LastException.Message)
					End Try
				End If

				' Observacion / incidencia
				Dim extras As String = ""
				If c.ContainsKey("observacion") Then extras = extras & "Observación: " & c.Get("observacion") & CRLF
				If c.ContainsKey("incidencia") Then extras = extras & "Incidencia: " & c.Get("incidencia") & CRLF

				Dim details As String = ""
				If responsesText <> "" Then details = details & responsesText
				If extras <> "" Then details = details & extras
				If details = "" Then details = "(sin detalles)"

				Dim lblResp As Label
				lblResp.Initialize("lblResp" & i)
				lblResp.Text = details
				lblResp.TextSize = 13
				Dim hResp As Int = EstimateLabelHeight(lblResp.Text, lblResp.TextSize, w - 16dip)

				card.AddView(lblResp, 8dip, 4dip + hCarName, w - 16dip, hResp)

				' ajustar altura del card según contenido
				Dim hCard As Int = hCarName + hResp + 12dip
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
