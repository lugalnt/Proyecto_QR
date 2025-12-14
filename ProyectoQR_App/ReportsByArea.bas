B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.1
@EndOfDesignText@
' ReportsByArea.bas
' Activity: lista reportes por área y al click guarda JSON temporal y abre ReportDetail
' Uso:
'   ReportsByArea.AreaToShow = "123"   ' string con Id o código de área
'   ReportsByArea.BaseUrlToUse = "https://tu-servidor/api/get_reports_by_area.php?area={area}"
'   StartActivity(ReportsByArea)
'
' Requisitos: OkHttpUtils2, JSON

#Region  Activity Attributes
    #FullScreen: False
    #IncludeTitle: True
#End Region

Sub Process_Globals
	Public AreaToShow As String
	Public BaseUrlToUse As String
End Sub

Sub Globals
	Private lv As ListView
	Private lblTitle As Label
	Private btnRefresh As Button
	Private reportsMap As Map ' index -> Map (report content)
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.RemoveAllViews
	Dim screenW As Int = 100%x

	lblTitle.Initialize("lblTitle")
	lblTitle.Text = "Reportes"
	lblTitle.TextSize = 16
	lblTitle.Gravity = Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL
	Activity.AddView(lblTitle, 0, 0, screenW, 10%y)

	btnRefresh.Initialize("btnRefresh")
	btnRefresh.Text = "Refrescar"
	Activity.AddView(btnRefresh, 2%x, 10%y + 4dip, 30%x, 8%y)

	lv.Initialize("lvReports")
	lv.SingleLineLayout.Label.TextColor = Colors.Black
	Activity.AddView(lv, 0, 18%y, screenW, 72%y)

	reportsMap.Initialize

	If AreaToShow <> "" Then
		lblTitle.Text = "Reportes del área: " & AreaToShow
		If BaseUrlToUse = "" Then
			BaseUrlToUse = "https://yourserver.example/api/get_reports_by_area.php?area={area}"
		End If
		FetchReports(BaseUrlToUse, AreaToShow)
	End If
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnRefresh_Click
	If AreaToShow = "" Or BaseUrlToUse = "" Then
		ToastMessageShow("Asigne AreaToShow y BaseUrlToUse antes de iniciar.", True)
		Return
	End If
	FetchReports(BaseUrlToUse, AreaToShow)
End Sub

' Build URL and call API
Sub FetchReports(BaseUrl As String, AreaCode As String)
	Dim url As String = BaseUrl
    If url.Contains("{area}") Then
        url = url.Replace("{area}", AreaCode)
    Else
        If url.Contains("?") Then
            url = url & "&area=" & AreaCode
        Else
            url = url & "?area=" & AreaCode
        End If
    End If
    
    ' --- SEGURIDAD: Enviar Id_Maquila si estamos logueados como maquila ---
    If Starter.Is_Maquila Then
        url = url & "&id_maquila=" & Starter.Id_Maquila
    End If
    ' ----------------------------------------------------------------------

	ProgressDialogShow("Cargando reportes...")
	Dim j As HttpJob
	j.Initialize("getReports", Me)
	j.Download(url)
End Sub

' Robust JobDone: limpia prefijos, detecta HTML, intenta parsear varias veces.
' Reemplaza tu JobDone por este y añade la función ExtractFirstJson (debajo)
' JobDone — versión consolidada y robusta
Sub JobDone(Job As HttpJob)
	ProgressDialogHide
	If Job.Success = False Then
		ToastMessageShow("Error al obtener reportes: " & Job.ErrorMessage, True)
		Log("JobDone: HTTP error: " & Job.ErrorMessage)
		Job.Release
		Return
	End If

	Dim res As String = Job.GetString
	If res = Null Then res = ""
	' quitar BOM si existiera
	Try
		res = res.Replace(Chr(65279), "")
	Catch
		Log("BOM replace error: " & LastException.Message)
	End Try
	res = res.Trim

	If res = "" Then
		ToastMessageShow("Respuesta vacía del servidor.", True)
		Log("JobDone: empty response")
		Job.Release
		Return
	End If

	' Guardar respuesta cruda para depuración
	Try
		File.WriteString(File.DirInternal, "reports_response_debug.json", res)
		Log("Saved raw response to: " & File.Combine(File.DirInternal, "reports_response_debug.json"))
	Catch
		Log("Error writing debug file: " & LastException.Message)
	End Try

	' Extraer el primer bloque JSON completo (objeto o array)
	Dim extracted As String = ExtractFirstJson(res)
	If extracted = "" Then
		Log("ExtractFirstJson returned empty. Response may be malformed. Response snippet: " & res.SubString2(0, Min(res.Length, 1000)))
		ToastMessageShow("No se detectó un bloque JSON completo en la respuesta (ver logs).", True)
		Job.Release
		Return
	End If

	' Guardar extracted para inspección (opcional)
	Try
		File.WriteString(File.DirInternal, "reports_response_extracted.json", extracted)
		Log("Saved extracted JSON to: " & File.Combine(File.DirInternal, "reports_response_extracted.json"))
	Catch
		Log("Error writing extracted debug file: " & LastException.Message)
	End Try

	' Limpiar controles invisibles: eliminar controles < 32 excepto tab(9),LF(10),CR(13)
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
		Else
			' Ignorar caracteres de control problemáticos
		End If
	Next
	Dim cleaned As String = sb.ToString

	If cleaned <> extracted Then
		Log("EXTRACTED cleaned: length from " & extracted.Length & " -> " & cleaned.Length)
	End If

	' Guardar cleaned para inspección
	Try
		File.WriteString(File.DirInternal, "reports_response_cleaned.json", cleaned)
		Log("Saved cleaned JSON to: " & File.Combine(File.DirInternal, "reports_response_cleaned.json"))
	Catch
		Log("Could not write cleaned JSON file: " & LastException.Message)
	End Try

	' ------------------------------------
	' Parsear UNA vez el JSON limpio (determinista)
	' ------------------------------------
	Dim parser As JSONParser
	Dim root As Object
	Try
		parser.Initialize(cleaned)
		If cleaned.StartsWith("{") Then
			Try
				root = parser.NextObject
				Log("Parsed cleaned as JSON Object")
			Catch
				Log("Parser NextObject failed: " & LastException.Message)
				root = Null
			End Try
		Else If cleaned.StartsWith("[") Then
			Try
				root = parser.NextArray
				Log("Parsed cleaned as JSON Array")
			Catch
				Log("Parser NextArray failed: " & LastException.Message)
				root = Null
			End Try
		Else
			Log("Cleaned does not start with { or [, cannot parse.")
			root = Null
		End If
	Catch
		Log("Parser initialize error: " & LastException.Message)
		root = Null
	End Try

	If root = Null Then
		Try
			Dim snippet As String = cleaned
			If snippet.Length > 2000 Then snippet = snippet.SubString2(0, 2000)
			Log("FINAL_PARSE_FAILED_SNIPPET: " & snippet)
		Catch
			Log("Could not produce FINAL_PARSE_FAILED_SNIPPET")
		End Try
		ToastMessageShow("No se pudo parsear la respuesta (ver Logcat).", True)
		Job.Release
		Return
	End If

	' --- root parseado correctamente ---
	Dim list As List
	list.Initialize

	Try
		If root Is List Then
			list = root
		Else If root Is Map Then
			Dim mp As Map = root
			If mp.ContainsKey("data") And mp.Get("data") Is List Then
				list = mp.Get("data")
			Else If mp.ContainsKey("reportes") And mp.Get("reportes") Is List Then
				list = mp.Get("reportes")
			Else
				' intentar encontrar primer arreglo dentro del map (iterar Keys sin castear a List)
				For Each k As String In mp.Keys
				Dim v As Object = mp.Get(k)
					If v Is List Then
					list = v
						Exit
						End If
					Next
				
				' si no encontramos lista, quizá la respuesta es un solo reporte con keys 'area'/'car_reports'/'JSON_Reporte'
				If (list.IsInitialized = False Or list.Size = 0) Then
					If mp.ContainsKey("area") Or mp.ContainsKey("car_reports") Or mp.ContainsKey("JSON_Reporte") Then
						list.Initialize
						list.Add(mp)
					End If
				End If
			End If
		End If
	Catch
		Log("JobDone building list error: " & LastException.Message)
		ToastMessageShow("Error procesando estructura de respuesta.", True)
		Job.Release
		Return
	End Try

	' Llenar ListView
	reportsMap.Clear
	lv.Clear

	For i = 0 To list.Size - 1
		Dim rawItem As Object = list.Get(i)
		Dim reportContent As Map
		reportContent = Null
		
		' Intentar sacar fecha del rawItem si es mapa
		Dim dateStr As String = ""
		If rawItem Is Map Then
			Dim rm As Map = rawItem
			If rm.ContainsKey("FechaRegistro_Reporte") Then dateStr = rm.Get("FechaRegistro_Reporte")
			If dateStr = "" And rm.ContainsKey("created_at") Then dateStr = rm.Get("created_at")
		End If

		If rawItem Is Map Then
			Dim candidate As Map = rawItem

			' Preferimos JSON_Reporte_parsed que devuelve la API (si existe)
			If candidate.ContainsKey("JSON_Reporte_parsed") Then
				Dim parsedObj As Object = candidate.Get("JSON_Reporte_parsed")
				If parsedObj Is Map Then
					reportContent = parsedObj
				Else If parsedObj Is List Then
					Dim pl As List = parsedObj
					If pl.Size > 0 And pl.Get(0) Is Map Then reportContent = pl.Get(0)
				End If
			End If

			' Si no viene parsed, intentar decodificar JSON_Reporte string
			If reportContent = Null And candidate.ContainsKey("JSON_Reporte") Then
				Try
					Dim sjson As String = candidate.Get("JSON_Reporte")
					If sjson <> "" Then
						Dim p As JSONParser
						p.Initialize(sjson)
						Dim maybe As Object = p.NextValue
						If maybe Is Map Then
							reportContent = maybe
						Else If maybe Is List Then
							Dim ltmp As List = maybe
							If ltmp.Size > 0 And ltmp.Get(0) Is Map Then reportContent = ltmp.Get(0)
						End If
					End If
				Catch
					Log("Error parsing JSON_Reporte: " & LastException.Message)
				End Try
			End If

			' Si sigue null, ver si el propio candidate tiene area/car_reports directamente
			If reportContent = Null Then
				If candidate.ContainsKey("area") Or candidate.ContainsKey("car_reports") Then
					reportContent = candidate
				End If
			End If
		Else
			' rawItem no es Map -> intentar parsearlo como JSON string
			Try
				Dim s2 As String = rawItem
				Dim p3 As JSONParser
				p3.Initialize(s2)
				Dim maybe2 As Object = p3.NextValue
				If maybe2 Is Map Then
					reportContent = maybe2
				End If
			Catch
				Log("Ignored non-map rawItem parse error: " & LastException.Message)
			End Try
		End If

		If reportContent = Null Then
			reportContent.Initialize
			reportContent.Put("raw", rawItem)
		End If

		' Determinar título
		Dim title As String = ""
		If reportContent.ContainsKey("area") Then
			Try
				Dim areaMap As Map = reportContent.Get("area")
				If areaMap.ContainsKey("area_name") Then title = areaMap.Get("area_name")
				If title = "" And areaMap.ContainsKey("areaName") Then title = areaMap.Get("areaName")
			Catch
				Log("Error leyendo area_name: " & LastException.Message)
			End Try
		End If

		If title = "" Then
			If reportContent.ContainsKey("Id_Reporte") Then
				title = "Reporte " & reportContent.Get("Id_Reporte")
			Else
				If reportContent.ContainsKey("id") Then
					title = "Reporte " & reportContent.Get("id")
				Else
					title = "Reporte " & (i + 1)
				End If
			End If
		End If
		
		' Agregar fecha al título para diferenciar
		If dateStr <> "" Then
			title = dateStr & " - " & title
		End If

		lv.AddSingleLine2(title, i)

		' Guardar un map con parsed + raw para no perder la fila completa
		Dim stored As Map
		stored.Initialize
		stored.Put("parsed", reportContent) ' Map (puede ser Null)
		If rawItem Is Map Then
			stored.Put("raw", rawItem) ' fila original devuelta por el servidor
		Else
			stored.Put("raw", Null)
		End If
		reportsMap.Put(i, stored)

	Next

	If lv.Size = 0 Then ToastMessageShow("No se encontraron reportes para el área.", False)
	Job.Release
End Sub



' Extrae el primer bloque JSON completo (objeto o array) de una cadena.
' Maneja comillas y escapes para no confundir llaves dentro de strings.
' Extrae el primer bloque JSON completo (objeto o array) de una cadena.
' Maneja comillas y escapes para no confundir llaves dentro de strings.
Sub ExtractFirstJson(s As String) As String
	If s = Null Then Return ""
	s = s.Trim
	Dim len As Int = s.Length
	If len = 0 Then Return ""

	' buscar el primer '{' o '['
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
					' devolvemos desde start hasta i inclusive
					Return s.SubString2(start, i + 1)
				End If
			End If
		End If
	Next

	Return "" ' no se encontró un bloque completo
End Sub




' Click en la lista -> guardar JSON temporal y abrir detalle
Sub lvReports_ItemClick (Position As Int, Value As Object)
	Dim idx As Int = Value
	Log("lvReports_ItemClick fired. idx=" & idx)
	ToastMessageShow("Click: " & idx, False) ' confirma visualmente

	If reportsMap.IsInitialized = False Then
		Log("lvReports_ItemClick: reportsMap no inicializado")
		MsgboxAsync("Mapa de reportes no inicializado", "Error")
		Return
	End If

	If reportsMap.ContainsKey(idx) = False Then
		Log("lvReports_ItemClick: reportsMap no contiene idx=" & idx)
		MsgboxAsync("Reporte no encontrado", "Error")
		Return
	End If

	Dim stored As Map
	stored = Null
	Try
		stored = reportsMap.Get(idx)
	Catch
		Log("lvReports_ItemClick: error al obtener stored: " & LastException.Message)
		MsgboxAsync("Reporte inválido", "Error")
		Return
	End Try

	If stored = Null Then
		Log("lvReports_ItemClick: stored es Null")
		MsgboxAsync("Reporte inválido", "Error")
		Return
	End If

	' Extraer parsed y raw de forma segura
	Dim parsed As Map = Null
	Dim raw As Map = Null
	Try
		If stored.ContainsKey("parsed") Then
			Dim tmpParsed As Object = stored.Get("parsed")
			If tmpParsed Is Map Then parsed = tmpParsed
		End If
	Catch
		Log("lvReports_ItemClick: error leyendo parsed: " & LastException.Message)
	End Try

	Try
		If stored.ContainsKey("raw") Then
			Dim tmpRaw As Object = stored.Get("raw")
			If tmpRaw Is Map Then raw = tmpRaw
		End If
	Catch
		Log("lvReports_ItemClick: error leyendo raw: " & LastException.Message)
	End Try

	' Si no hay parsed, usar raw
	If parsed = Null And raw Is Map Then parsed = raw

	If parsed = Null Then
		Log("lvReports_ItemClick: no hay parsed ni raw útiles")
		MsgboxAsync("No hay datos válidos para este reporte.", "Error")
		Return
	End If

	' Construir out con area y car_reports (busca en parsed, luego en raw.JSON_Reporte_parsed, luego en raw.JSON_Reporte)
	Dim out As Map
	out.Initialize

	Try
		If parsed.ContainsKey("area") Then out.Put("area", parsed.Get("area"))
	Catch
		Log("lvReports_ItemClick: error copiando parsed.area: " & LastException.Message)
	End Try
	Try
		If parsed.ContainsKey("car_reports") Then out.Put("car_reports", parsed.Get("car_reports"))
	Catch
		Log("lvReports_ItemClick: error copiando parsed.car_reports: " & LastException.Message)
	End Try

	If (out.ContainsKey("area") = False Or out.ContainsKey("car_reports") = False) Then
		If raw Is Map Then
			' JSON_Reporte_parsed directo
			If raw.ContainsKey("JSON_Reporte_parsed") Then
				Try
					Dim jrMap As Map = raw.Get("JSON_Reporte_parsed")
					If jrMap <> Null Then
						If out.ContainsKey("area") = False And jrMap.ContainsKey("area") Then
							out.Put("area", jrMap.Get("area"))
						End If
						If out.ContainsKey("car_reports") = False And jrMap.ContainsKey("car_reports") Then
							out.Put("car_reports", jrMap.Get("car_reports"))
						End If
					End If
				Catch
					Log("lvReports_ItemClick: raw.JSON_Reporte_parsed no es Map o error: " & LastException.Message)
				End Try
			End If

			' JSON_Reporte string (parsear)
			If (out.ContainsKey("area") = False Or out.ContainsKey("car_reports") = False) And raw.ContainsKey("JSON_Reporte") Then
				Try
					Dim s As String = raw.Get("JSON_Reporte")
					If s <> "" Then
						Dim p As JSONParser
						p.Initialize(s)
						Dim maybe As Object = p.NextValue
						If maybe Is Map Then
							Dim m As Map = maybe
							If out.ContainsKey("area") = False And m.ContainsKey("area") Then out.Put("area", m.Get("area"))
							If out.ContainsKey("car_reports") = False And m.ContainsKey("car_reports") Then out.Put("car_reports", m.Get("car_reports"))
						End If
					End If
				Catch
					Log("lvReports_ItemClick: error parseando JSON_Reporte: " & LastException.Message)
				End Try
			End If
		End If
	End If

	' Copiar metadatos desde raw
	If raw Is Map Then
		Try
			For Each k As String In raw.Keys
			Try
				If out.ContainsKey(k) = False Then out.Put(k, raw.Get(k))
					Catch
				Log("lvReports_ItemClick: no pudo copiar key " & k & ": " & LastException.Message)
					End Try
				Next
			Catch
		Log("lvReports_ItemClick: error iterando keys raw: " & LastException.Message)
			End Try
		End If
	

	' Asegurar Id_Reporte
	If out.ContainsKey("Id_Reporte") = False Then
		If raw Is Map And raw.ContainsKey("Id_Reporte") Then out.Put("Id_Reporte", raw.Get("Id_Reporte"))
	End If

	' Guardar y hacer log del JSON final (snippet)
	Try
		Dim gen As JSONGenerator
		gen.Initialize(out)
		Dim jsonStr As String = gen.ToString
		File.WriteString(File.DirInternal, "current_report.json", jsonStr)
		Log("lvReports_ItemClick: wrote current_report.json length=" & jsonStr.Length)
		Log("lvReports_ItemClick snippet: " & jsonStr.SubString2(0, Min(800, jsonStr.Length)))
	Catch
		Log("lvReports_ItemClick: Error writing current_report.json: " & LastException.Message)
		MsgboxAsync("No se pudo preparar el detalle del reporte.", "Error")
		Return
	End Try

	ReportDetail.AllowEdit = False
	StartActivity(ReportDetail)
End Sub






