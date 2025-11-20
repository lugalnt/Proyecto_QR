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

	ProgressDialogShow("Cargando reportes...")
	Dim j As HttpJob
	j.Initialize("getReports", Me)
	j.Download(url)
End Sub

' Robust JobDone: limpia prefijos, detecta HTML, intenta parsear varias veces.
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
	' eliminar BOM invisibles
	Try
		res = res.Replace(Chr(65279), "")
	Catch
		Log("JobDone: BOM replace failed: " & LastException.Message)
	End Try
	res = res.Trim

	If res = "" Then
		ToastMessageShow("Respuesta vacía del servidor.", True)
		Log("JobDone: empty response")
		Job.Release
		Return
	End If

	' Detectar HTML (si el primer caracter significativo es '<')
	Dim firstNonSpace As Int = -1
	For i = 0 To res.Length - 1
		Dim c As String = res.SubString2(i, i + 1)
		If c <> " " And c <> Chr(10) And c <> Chr(13) And c <> Chr(9) Then
			firstNonSpace = i
			Exit
		End If
	Next
	If firstNonSpace >= 0 Then
		Dim c2 As String = res.SubString2(firstNonSpace, firstNonSpace + 1)
		If c2 = "<" Then
			ToastMessageShow("Respuesta HTML recibida en lugar de JSON (ver logs).", True)
			Log("JobDone: HTML response snippet: " & res.SubString2(0, Min(res.Length, 500)))
			Job.Release
			Return
		End If
	End If

	' localizar inicio JSON
	Dim idxObj As Int = res.IndexOf("{")
	Dim idxArr As Int = res.IndexOf("[")
	Dim startIdx As Int = -1
	If idxObj = -1 Then
		startIdx = idxArr
	Else
		If idxArr = -1 Then
			startIdx = idxObj
		Else
			startIdx = Min(idxObj, idxArr)
		End If
	End If

	Dim resToParse As String
	If startIdx > 0 Then
		resToParse = res.SubString(startIdx)
		Log("JobDone: trimming response before first JSON char. trimmed snippet: " & resToParse.SubString2(0, Min(resToParse.Length, 200)))
	Else
		resToParse = res
	End If

	Dim parser As JSONParser
	parser.Initialize(resToParse)
	Dim root As Object
	Try
		root = parser.NextValue
	Catch
		Log("JobDone parse error first attempt: " & LastException.Message)
		' segundo intento con respuesta original
		Try
			parser.Initialize(res)
			root = parser.NextValue
		Catch
			Log("JobDone parse error second attempt: " & LastException.Message)
			Dim low As String = res.ToLowerCase
			If low = "null" Or low = "true" Or low = "false" Or IsNumber(res) Then
				ToastMessageShow("Respuesta válida pero no es un objeto/array JSON esperado.", True)
				Log("JobDone: primitive response: " & res)
				Job.Release
				Return
			End If
			Dim snippet As String = res.SubString2(0, Min(res.Length, 800))
			ToastMessageShow("No se pudo parsear JSON (mira logs).", True)
			Log("JobDone final parse fail. Response snippet: " & snippet)
			Job.Release
			Return
		End Try
	End Try

	' --- root parseado ---
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
				' intentar encontrar primer arreglo dentro del map
				Dim keys As List = mp.Keys
				For Each k As String In keys
					Dim v As Object = mp.Get(k)
					If v Is List Then
						list = v
						Exit
					End If
				Next
				' si no encontramos lista, quizá la respuesta es un solo reporte con keys 'area'/'car_reports'
				If (list.IsInitialized = False Or list.Size = 0) Then
					If mp.ContainsKey("area") Or mp.ContainsKey("car_reports") Then
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

		If rawItem Is Map Then
			Dim candidate As Map = rawItem
			If candidate.ContainsKey("area") Or candidate.ContainsKey("car_reports") Then
				reportContent = candidate
			Else
				Dim possibleFields As List
				possibleFields = Array As String("JSON_Reporte","JSON","reporte_json","Contenido","Reporte","data")
				For Each f As String In possibleFields
					If candidate.ContainsKey(f) Then
						Try
							Dim s As String = candidate.Get(f)
							If s <> "" Then
								Dim p2 As JSONParser
								p2.Initialize(s)
								Dim maybe As Object
								maybe = p2.NextValue
								If maybe Is Map Then
									reportContent = maybe
									Exit
								End If
								If maybe Is List Then
									Dim l2 As List = maybe
									If l2.Size > 0 And l2.Get(0) Is Map Then
										reportContent = l2.Get(0)
										Exit
									End If
								End If
							End If
						Catch
							Log("Ignored parse error for field " & f & ": " & LastException.Message)
						End Try
					End If
				Next
			End If
		Else
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
			Else If reportContent.ContainsKey("id") Then
				title = "Reporte " & reportContent.Get("id")
			Else
				title = "Reporte " & (i + 1)
			End If
		End If

		lv.AddSingleLine2(title, i)
		reportsMap.Put(i, reportContent)
	Next

	If lv.Size = 0 Then ToastMessageShow("No se encontraron reportes para el área.", False)
	Job.Release
End Sub

' Click en la lista -> guardar JSON temporal y abrir detalle
Sub lv_ItemClick (Position As Int, Value As Object)
	Dim idx As Int = Value
	Dim rep As Map = reportsMap.Get(idx)
	If rep = Null Then
		MsgboxAsync("Reporte inválido", "Error")
		Return
	End If

	Try
		Dim gen As JSONGenerator
		gen.Initialize(rep)
		Dim jsonStr As String = gen.ToString
		File.WriteString(File.DirInternal, "current_report.json", jsonStr)
	Catch
		Log("Error writing current_report.json: " & LastException.Message)
		MsgboxAsync("No se pudo preparar el detalle del reporte.", "Error")
		Return
	End Try

	StartActivity(ReportDetail) ' ReportDetail debe existir en el proyecto y leer current_report.json
End Sub
