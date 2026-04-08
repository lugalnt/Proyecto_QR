B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.1
@EndOfDesignText@
#Region  Activity Attributes
	#FullScreen: True
	#IncludeTitle: False
#End Region

' ============================================================
' menuprincipal_v2.bas  —  Menú principal con UI en código puro
' Funcionalidad idéntica a menuprincipal.bas
' Nombres de variables y eventos preservados al 100%
' ============================================================

Sub Process_Globals
	Dim BaseUrl As String = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api"
End Sub

Sub Globals
	Dim Nombre_Usuario As String = Starter.Nombre_Usuario
	Dim JsonArea As String = ""

	' ---- Controles del .bal original — mismos nombres ----
	Private Scanner As GoogleCodeScanner
	Private LB_Bienvenido_NombreUsuario As Label

	' ET_DatosArea conserva el nombre pero es Label para mejor diseño.
	' Ambos tipos tienen .Text → la lógica de JobDone no cambia.
	Private ET_DatosArea As Label

	' ---- Botones (mismos nombres → mismos eventos Click) ----
	Private BTN_EscanearArea    As Button
	Private BTN_IniciarReporte  As Button
	Private BTN_MisReportes     As Button

	' ---- Panel de info del área (se muestra sólo tras escanear) ----
	Private pnlAreaInfo As Panel
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.RemoveAllViews

	Dim screenW As Int = 100%x
	Dim screenH As Int = 100%y
	Dim outerPad As Int = 14dip

	' ============================================================
	' HEADER  —  barra azul Material con saludo al usuario
	' ============================================================
	Dim headerH As Int = 13%y

	Dim pnlHeader As Panel
	pnlHeader.Initialize("")
	pnlHeader.Color = Colors.RGB(25, 118, 210)   ' azul Material 700
	Activity.AddView(pnlHeader, 0, 0, screenW, headerH)

	' Texto "Bienvenido:"
	Dim lblBienvenido As Label
	lblBienvenido.Initialize("")
	lblBienvenido.Text = "Bienvenido:"
	lblBienvenido.TextSize = 16
	lblBienvenido.Typeface = Typeface.DEFAULT_BOLD
	lblBienvenido.TextColor = Colors.White
	lblBienvenido.Gravity = Gravity.LEFT + Gravity.CENTER_VERTICAL
	pnlHeader.AddView(lblBienvenido, outerPad, 0, 44%x, headerH)

	' Nombre del usuario — mismo nombre de variable que .bal
	LB_Bienvenido_NombreUsuario.Initialize("LB_Bienvenido_NombreUsuario")
	LB_Bienvenido_NombreUsuario.Text = Nombre_Usuario
	LB_Bienvenido_NombreUsuario.TextSize = 16
	LB_Bienvenido_NombreUsuario.TextColor = Colors.ARGB(230, 255, 255, 255)
	LB_Bienvenido_NombreUsuario.Gravity = Gravity.LEFT + Gravity.CENTER_VERTICAL
	pnlHeader.AddView(LB_Bienvenido_NombreUsuario, outerPad + 46%x, 0, screenW - outerPad - 46%x, headerH)

	' ============================================================
	' CUERPO  —  fondo blanco suave
	' ============================================================
	Dim contentTop As Int = headerH
	Dim contentH   As Int = screenH - headerH

	Dim pnlBody As Panel
	pnlBody.Initialize("")
	pnlBody.Color = Colors.ARGB(255, 247, 249, 252)
	Activity.AddView(pnlBody, 0, contentTop, screenW, contentH)

	' Posiciones dentro del cuerpo (relativas a pnlBody, top=0)
	Dim btnY  As Int = 4%y     ' primer botón empieza aquí
	Dim btnH  As Int = 13%y    ' alto de botón grande
	Dim btnGap As Int = 10dip  ' separación entre botones

	' ============================================================
	' BOTÓN 1: Escanear Área  —  ancho completo, protagonista
	' Nombre de evento preservado: BTN_EscanearArea_Click
	' ============================================================
	BTN_EscanearArea.Initialize("BTN_EscanearArea")
	BTN_EscanearArea.Text = Chr(128247) & "  Escanear Área QR"
	BTN_EscanearArea.TextSize = 15
	BTN_EscanearArea.Color = Colors.RGB(25, 118, 210)    ' azul
	BTN_EscanearArea.TextColor = Colors.White
	pnlBody.AddView(BTN_EscanearArea, outerPad, btnY, screenW - (outerPad * 2), btnH)

	btnY = btnY + btnH + btnGap

	' ============================================================
	' FILA 2: Iniciar Reporte  |  Mis Reportes
	' Mitad del ancho cada uno, separados por un gap interno
	' ============================================================
	Dim halfGap As Int = 6dip
	Dim halfW   As Int = (screenW - (outerPad * 2) - halfGap) / 2

	' --- Iniciar Reporte ---
	BTN_IniciarReporte.Initialize("BTN_IniciarReporte")
	BTN_IniciarReporte.Text = Chr(9654) & "  Iniciar Reporte"
	BTN_IniciarReporte.TextSize = 13
	BTN_IniciarReporte.Color = Colors.RGB(46, 125, 50)   ' verde Material
	BTN_IniciarReporte.TextColor = Colors.White
	pnlBody.AddView(BTN_IniciarReporte, outerPad, btnY, halfW, btnH)

	' --- Mis Reportes ---
	BTN_MisReportes.Initialize("BTN_MisReportes")
	BTN_MisReportes.Text = Chr(128203) & "  Mis Reportes"
	BTN_MisReportes.TextSize = 13
	BTN_MisReportes.Color = Colors.RGB(69, 90, 100)      ' gris azulado (Blue Grey 700)
	BTN_MisReportes.TextColor = Colors.White
	pnlBody.AddView(BTN_MisReportes, outerPad + halfW + halfGap, btnY, halfW, btnH)

	btnY = btnY + btnH + btnGap + 2dip

	' ============================================================
	' SEPARADOR VISUAL
	' ============================================================
	Dim pnlSep As Panel
	pnlSep.Initialize("")
	pnlSep.Color = Colors.ARGB(60, 0, 0, 0)
	pnlBody.AddView(pnlSep, outerPad, btnY, screenW - (outerPad * 2), 1dip)

	btnY = btnY + 8dip

	' ============================================================
	' TARJETA DE INFORMACIÓN DEL ÁREA
	' Se muestra siempre, pero vacía hasta que el usuario escanea.
	' ET_DatosArea es Label con mismo nombre → JobDone sigue igual.
	' ============================================================
	Dim infoCardH As Int = contentH - btnY - outerPad

	pnlAreaInfo.Initialize("pnlAreaInfo")
	pnlAreaInfo.Color = Colors.White
	pnlBody.AddView(pnlAreaInfo, outerPad, btnY, screenW - (outerPad * 2), infoCardH)

	' Barra azul de acento a la izquierda (estilo card consistente con ReportDetail)
	Dim pnlAccent As Panel
	pnlAccent.Initialize("")
	pnlAccent.Color = Colors.RGB(25, 118, 210)
	pnlAreaInfo.AddView(pnlAccent, 0, 0, 5dip, infoCardH)

	' Ícono/label de instrucción (visible antes de escanear)
	Dim lblHint As Label
	lblHint.Initialize("")
	lblHint.Text = "Escanea un área para ver su información"
	lblHint.TextSize = 12
	lblHint.TextColor = Colors.RGB(160, 160, 160)
	lblHint.Gravity = Gravity.CENTER
	pnlAreaInfo.AddView(lblHint, 10dip, 0, (screenW - (outerPad * 2)) - 15dip, 40dip)

	' ET_DatosArea: Label con nombre original — recibe el texto en JobDone
	ET_DatosArea.Initialize("ET_DatosArea")
	ET_DatosArea.Text = ""
	ET_DatosArea.TextSize = 13
	ET_DatosArea.TextColor = Colors.RGB(40, 40, 40)
	ET_DatosArea.Gravity = Gravity.LEFT + Gravity.TOP
	pnlAreaInfo.AddView(ET_DatosArea, 10dip, 46dip, (screenW - (outerPad * 2)) - 15dip, infoCardH - 50dip)

	' ============================================================
	' Inicializar el escáner QR
	' ============================================================
	Scanner.Initialize

End Sub

Sub Activity_Resume
End Sub

Sub Activity_Pause (UserClosed As Boolean)
End Sub

' ============================================================
' Utilidad: codificación URL
' ============================================================
Sub URLEncode(s As String) As String
	Dim jo As JavaObject
	jo.InitializeStatic("java.net.URLEncoder")
	Dim result As String
	result = jo.RunMethod("encode", Array(s, "UTF-8"))
	Return result
End Sub

' ============================================================
' LÓGICA DE NEGOCIO — idéntica a menuprincipal.bas, sin cambios
' ============================================================

Private Sub BTN_IniciarReporte_Click
	If JsonArea <> "" Then
		Try
			File.WriteString(File.DirInternal, "last_area.json", JsonArea)
		Catch
			Log("Error guardando last_area.json desde BTN_IniciarReporte_Click: " & LastException.Message)
		End Try
		StartActivity(ReportDialog)
	Else
		MsgboxAsync("Escanea un área primero", "Reporte")
	End If
End Sub

Sub JobDone(Job As HttpJob)
	ProgressDialogHide
	If Job.Success Then
		If Job.JobName = "JobArea" Then
			Dim res As String
			res = Job.GetString
			Log("Respuesta API getArea: " & res)

			Dim startIndex As Int
			startIndex = res.IndexOf("{")
			If startIndex > -1 Then
				res = res.SubString(startIndex)
			End If

			Dim parser As JSONParser
			parser.Initialize(res)
			Try
				Dim root As Map
				root = parser.NextObject

				Dim data As Map
				data.Initialize
				If root.ContainsKey("success") Then
					If root.Get("success") = True Then
						If root.ContainsKey("data") Then
							data = root.Get("data")
						Else
							data.Initialize
						End If
					Else
						Dim msgFail As String
						msgFail = "Área no encontrada"
						If root.ContainsKey("message") Then msgFail = root.Get("message")
						ToastMessageShow(msgFail, True)
						Job.Release
						Return
					End If
				Else
					data = root
				End If

				Dim nombre As String
				nombre = ""
				If data.ContainsKey("Nombre_Area") Then
					nombre = data.Get("Nombre_Area")
				Else If data.ContainsKey("Nombre") Then
					nombre = data.Get("Nombre")
				Else If data.ContainsKey("NombreArea") Then
					nombre = data.Get("NombreArea")
				End If

				If data.ContainsKey("Id_Area") Then
					Starter.Id_Area = data.Get("Id_Area")
				End If

				Dim descripcion As String
				descripcion = ""
				If data.ContainsKey("Descripcion_Area") Then
					descripcion = data.Get("Descripcion_Area")
				Else If data.ContainsKey("Descripcion") Then
					descripcion = data.Get("Descripcion")
				Else If data.ContainsKey("DescripcionArea") Then
					descripcion = data.Get("DescripcionArea")
				End If

				Dim numeroCAR As String
				numeroCAR = ""
				If data.ContainsKey("NumeroCAR_Area") Then
					numeroCAR = data.Get("NumeroCAR_Area")
				Else If data.ContainsKey("NumeroCAR") Then
					numeroCAR = data.Get("NumeroCAR")
				Else If data.ContainsKey("Numero_CAR") Then
					numeroCAR = data.Get("Numero_CAR")
				End If

				If data.ContainsKey("JSON_Area") Then
					Dim jsonAreaStr As String
					jsonAreaStr = ""
					Try
						jsonAreaStr = data.Get("JSON_Area")
					Catch
						jsonAreaStr = ""
					End Try
					If jsonAreaStr = "" Then
						Try
							Dim jg As JSONGenerator
							jg.Initialize(data.Get("JSON_Area"))
							jsonAreaStr = jg.ToString
						Catch
							jsonAreaStr = ""
						End Try
					End If
					If jsonAreaStr <> "" Then
						JsonArea = jsonAreaStr
						Try
							File.WriteString(File.DirInternal, "last_area.json", JsonArea)
						Catch
							Log("Error guardando last_area.json: " & LastException.Message)
						End Try
					End If
				End If

				' ---- Mostrar info en la tarjeta (mismo texto que original) ----
				Dim texto As String
				texto = "Nombre del Area: " & nombre & CRLF
				texto = texto & "Descripción: " & descripcion & CRLF
				texto = texto & "Numero C.A.R: " & numeroCAR
				ET_DatosArea.Text = texto   ' Label.Text = igual que EditText.Text

			Catch
				ToastMessageShow("Respuesta inválida del servidor", True)
				Log("Error parse JSON getArea: " & LastException.Message)
			End Try
		End If
	Else
		ToastMessageShow("Error de red: " & Job.ErrorMessage, True)
	End If
	Job.Release
End Sub

Sub ConsultarArea(codigo As String)
	ProgressDialogShow("Buscando área...")
	Dim job As HttpJob
	job.Initialize("JobArea", Me)
	Dim body As String
	body = "codigo=" & URLEncode(codigo)
	job.PostString(BaseUrl & "/getArea.php", body)
End Sub

Private Sub BTN_EscanearArea_Click
	Dim formats As List = Array(Scanner.FORMAT_ALL_FORMATS)
	Wait For (Scanner.Scan(formats)) Complete (Result As ScannerResult)
	If Result.Success Then
		ConsultarArea(Result.Value)
	End If
End Sub

Private Sub BTN_MisReportes_Click
	If Starter.Id_Usuario <> 0 Then
		ReportsByUser.UserToShow = Starter.Id_Usuario
		ReportsByUser.BaseUrlToUse = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api/get_reports_by_user.php?user={user}"
		StartActivity(ReportsByUser)
	Else
		ToastMessageShow("No hay usuario identificado.", True)
	End If
End Sub
