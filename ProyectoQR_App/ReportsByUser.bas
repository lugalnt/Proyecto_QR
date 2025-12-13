B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.1
@EndOfDesignText@
' ReportsByUser.bas
' Activity: lista reportes por usuario
#Region  Activity Attributes
    #FullScreen: False
    #IncludeTitle: True
#End Region

Sub Process_Globals
	Public UserToShow As String
	Public BaseUrlToUse As String
End Sub

Sub Globals
	Private lv As ListView
	Private lblTitle As Label
	Private btnRefresh As Button
	Private reportsMap As Map
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.RemoveAllViews
	Dim screenW As Int = 100%x

	lblTitle.Initialize("lblTitle")
	lblTitle.Text = "Mis Reportes"
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

	If UserToShow <> "" Then
		lblTitle.Text = "Reportes de Usuario: " & UserToShow
		If BaseUrlToUse = "" Then
			BaseUrlToUse = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api/get_reports_by_user.php?user={user}"
		End If
		FetchReports(BaseUrlToUse, UserToShow)
	Else
		' Intentar usar Starter.Id_Usuario
		Try
			If Starter.Id_Usuario <> "" Then
				UserToShow = Starter.Id_Usuario
				lblTitle.Text = "Mis Reportes"
				If BaseUrlToUse = "" Then
					BaseUrlToUse = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api/get_reports_by_user.php?user={user}"
				End If
				FetchReports(BaseUrlToUse, UserToShow)
			End If
		Catch
			Log("Error getting Starter.Id_Usuario")
		End Try
	End If
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnRefresh_Click
	If UserToShow <> "" And BaseUrlToUse <> "" Then
		FetchReports(BaseUrlToUse, UserToShow)
	Else
		ToastMessageShow("No hay usuario definido.", True)
	End If
End Sub

Sub FetchReports(BaseUrl As String, UserId As String)
	Dim url As String = BaseUrl
	If url.Contains("{user}") Then
		url = url.Replace("{user}", UserId)
	Else
		If url.Contains("?") Then
			url = url & "&user=" & UserId
		Else
			url = url & "?user=" & UserId
		End If
	End If

	ProgressDialogShow("Cargando mis reportes...")
	Dim j As HttpJob
	j.Initialize("getReports", Me)
	j.Download(url)
End Sub

Sub JobDone(Job As HttpJob)
	ProgressDialogHide
	If Job.Success = False Then
		ToastMessageShow("Error: " & Job.ErrorMessage, True)
		Job.Release
		Return
	End If

	Dim res As String = Job.GetString
	' Limpieza básica y parseo similar a ReportsByArea...
	' (Para abreviar, uso lógica simplificada pero robusta)
	
	Dim p As Int = res.IndexOf("{")
	Dim p2 As Int = res.IndexOf("[")
	If p = -1 And p2 = -1 Then
		ToastMessageShow("Respuesta inválida", True)
		Job.Release
		Return
	End If
	If p > -1 And (p2 = -1 Or p < p2) Then res = res.SubString(p) Else res = res.SubString(p2)

	Dim parser As JSONParser
	parser.Initialize(res)
	Dim root As Object
	Try
		If res.StartsWith("[") Then
			root = parser.NextArray
		Else
			root = parser.NextObject
		End If
	Catch
		ToastMessageShow("Error JSON", True)
		Job.Release
		Return
	End Try

	Dim list As List
	list.Initialize

	If root Is Map Then
		Dim m As Map = root
		If m.ContainsKey("data") Then
			Dim d As Object = m.Get("data")
			If d Is List Then list = d
		Else If m.ContainsKey("success") And m.Get("success") = True Then
			' A veces devuelve success=true y data vacío
		End If
	Else If root Is List Then
		list = root
	End If

	reportsMap.Clear
	lv.Clear

	For i = 0 To list.Size - 1
		Dim item As Map = list.Get(i)
		
		' Título: Fecha + Area Name
		Dim title As String = ""
		If item.ContainsKey("FechaRegistro_Reporte") Then title = item.Get("FechaRegistro_Reporte")
		
		' Intentar sacar nombre area
		Dim areaName As String = ""
		If item.ContainsKey("JSON_Reporte") Then
			Dim s As String = item.Get("JSON_Reporte")
			Try
				Dim jp2 As JSONParser
				jp2.Initialize(s)
				Dim mr As Map = jp2.NextObject
				If mr.ContainsKey("area") Then
					Dim ma As Map = mr.Get("area")
					If ma.ContainsKey("area_name") Then areaName = ma.Get("area_name")
				End If
			Catch
				Log(LastException.Message)
			End Try
		End If
		
		If areaName <> "" Then title = title & " - " & areaName
		
		lv.AddSingleLine2(title, i)
		reportsMap.Put(i, item)
	Next
	
	If lv.Size = 0 Then ToastMessageShow("No tienes reportes.", False)
	Job.Release
End Sub

Sub lvReports_ItemClick (Position As Int, Value As Object)
	Dim idx As Int = Value
	Dim item As Map = reportsMap.Get(idx)
	
	' Guardar current_report.json
	Dim jg As JSONGenerator
	jg.Initialize(item)
	File.WriteString(File.DirInternal, "current_report.json", jg.ToString)
	
	ReportDetail.AllowEdit = True
	StartActivity(ReportDetail)
End Sub
