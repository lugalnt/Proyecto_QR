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
' login_v2.bas  —  Login con UI generada en código puro
' Funcionalidad idéntica a login.bas / loginscreen.bal
' Nombres de variables y eventos preservados al 100%
' ============================================================

Sub Process_Globals
	Dim BaseUrl As String = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api"
End Sub

Sub Globals
	' Mismos nombres que en el .bal original — la lógica no cambia
	Private Nombre_Usuario As EditText
	Private Password_Usuario As EditText

	' Referencia al botón de login (mismo nombre de evento: Button_Submit_Click)
	Private Button_Submit As Button

	' Paneles de UI
	Private pnlBg As Panel
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.RemoveAllViews

	Dim screenW As Int = 100%x
	Dim screenH As Int = 100%y

	' ============================================================
	' FONDO: simulación de gradiente cálido en 3 bandas
	' Banda superior: rojo oscuro  →  media: rojo-naranja  →  inferior: naranja cálido
	' ============================================================
	pnlBg.Initialize("")
	pnlBg.Color = Colors.RGB(198, 40, 40)   ' rojo profundo (banda superior dominante)
	Activity.AddView(pnlBg, 0, 0, screenW, screenH)

	' Banda media (50% → 75%)
	Dim pnlMid As Panel
	pnlMid.Initialize("")
	pnlMid.Color = Colors.RGB(216, 67, 21)   ' rojo-naranja
	pnlBg.AddView(pnlMid, 0, 50%y, screenW, 25%y)

	' Banda inferior (75% → 100%)
	Dim pnlBot As Panel
	pnlBot.Initialize("")
	pnlBot.Color = Colors.RGB(230, 81, 0)    ' naranja oscuro
	pnlBg.AddView(pnlBot, 0, 75%y, screenW, 25%y)

	' ============================================================
	' LOGO / BRANDING  —  centrado, parte alta
	' Rectángulo blanco con texto "FCS" en negrita
	' ============================================================
	Dim logoW  As Int = 42%x
	Dim logoH  As Int = 8%y
	Dim logoX  As Int = (screenW - logoW) / 2
	Dim logoY  As Int = 9%y

	Dim pnlLogo As Panel
	pnlLogo.Initialize("")
	pnlLogo.Color = Colors.White
	pnlBg.AddView(pnlLogo, logoX, logoY, logoW, logoH)

	Dim lblLogo As Label
	lblLogo.Initialize("")
	lblLogo.Text = "FCS"
	lblLogo.TextSize = 26
	lblLogo.Typeface = Typeface.DEFAULT_BOLD
	lblLogo.TextColor = Colors.RGB(198, 40, 40)
	lblLogo.Gravity = Gravity.CENTER
	pnlLogo.AddView(lblLogo, 0, 0, logoW, logoH)

	' ============================================================
	' TARJETA BLANCA  —  contiene los campos y el botón
	' Posicionada en el tercio central de la pantalla
	' ============================================================
	Dim cardW  As Int = 84%x
	Dim cardH  As Int = 48%y
	Dim cardX  As Int = (screenW - cardW) / 2
	Dim cardY  As Int = 22%y          ' justo debajo del logo

	Dim pnlCard As Panel
	pnlCard.Initialize("")
	pnlCard.Color = Colors.ARGB(240, 255, 255, 255)   ' blanco con leve transparencia
	pnlBg.AddView(pnlCard, cardX, cardY, cardW, cardH)

	' Coord. dentro de la tarjeta (todo relativo a 0,0 de pnlCard)
	Dim hPad  As Int = 20dip
	Dim fW    As Int = cardW - (hPad * 2)     ' ancho de campo
	Dim top   As Int = 4%y                     ' margen superior dentro de la tarjeta

	' ---- Label: Nombre de Usuario ----
	Dim lblUser As Label
	lblUser.Initialize("")
	lblUser.Text = "Nombre de Usuario"
	lblUser.TextSize = 12
	lblUser.TextColor = Colors.RGB(110, 110, 110)
	pnlCard.AddView(lblUser, hPad, top, fW, 22dip)

	' ---- EditText: Nombre_Usuario ----
	Nombre_Usuario.Initialize("Nombre_Usuario")
	Nombre_Usuario.Hint = "usuario"
	Nombre_Usuario.TextSize = 15
	Nombre_Usuario.SingleLine = True
	pnlCard.AddView(Nombre_Usuario, hPad, top + 24dip, fW, 44dip)

	' Separador bajo el campo
	Dim sep1 As Panel
	sep1.Initialize("")
	sep1.Color = Colors.RGB(198, 40, 40)
	pnlCard.AddView(sep1, hPad, top + 24dip + 44dip, fW, 2dip)

	top = top + 24dip + 44dip + 2dip + 18dip   ' avanzar

	' ---- Label: Contraseña ----
	Dim lblPass As Label
	lblPass.Initialize("")
	lblPass.Text = "Contraseña"
	lblPass.TextSize = 12
	lblPass.TextColor = Colors.RGB(110, 110, 110)
	pnlCard.AddView(lblPass, hPad, top, fW, 22dip)

	' ---- EditText: Password_Usuario ----
	Password_Usuario.Initialize("Password_Usuario")
	Password_Usuario.Hint = "••••••••"
	Password_Usuario.TextSize = 15
	Password_Usuario.SingleLine = True
	Password_Usuario.Password = True
	pnlCard.AddView(Password_Usuario, hPad, top + 24dip, fW, 44dip)

	' Separador bajo el campo
	Dim sep2 As Panel
	sep2.Initialize("")
	sep2.Color = Colors.RGB(198, 40, 40)
	pnlCard.AddView(sep2, hPad, top + 24dip + 44dip, fW, 2dip)

	top = top + 24dip + 44dip + 2dip + 28dip   ' espacio extra antes del botón

	' ---- Botón de ingreso ----
	' Nombre de evento: Button_Submit_Click  (igual que en login.bas)
	Button_Submit.Initialize("Button_Submit")
	Button_Submit.Text = "Iniciar Sesión"
	Button_Submit.TextSize = 15
	Button_Submit.Color = Colors.RGB(198, 40, 40)
	Button_Submit.TextColor = Colors.White
	pnlCard.AddView(Button_Submit, hPad, top, fW, 48dip)

	' ============================================================
	' TEXTO DE PIE  —  versión / copyright debajo de la tarjeta
	' ============================================================
	Dim lblFooter As Label
	lblFooter.Initialize("")
	lblFooter.Text = "Sistema FCS  •  Control de Actividades"
	lblFooter.TextSize = 10
	lblFooter.TextColor = Colors.ARGB(180, 255, 255, 255)
	lblFooter.Gravity = Gravity.CENTER
	Dim footerY As Int = cardY + cardH + 3%y
	If footerY + 20dip < screenH Then
		pnlBg.AddView(lblFooter, 0, footerY, screenW, 20dip)
	End If

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
' LÓGICA DE NEGOCIO — idéntica a login.bas, sin cambios
' ============================================================
Private Sub Button_Submit_Click
	Dim usuario As String = Nombre_Usuario.Text.Trim
	Dim pass As String = Password_Usuario.Text

	If usuario = "" Or pass = "" Then
		ToastMessageShow("Completa ambos campos", True)
		Return
	End If

	ProgressDialogShow("Iniciando sesión...")

	Dim job As HttpJob
	job.Initialize("LoginJob", Me)
	Dim body As String
	body = "nombre_usuario=" & URLEncode(usuario) & "&password=" & URLEncode(pass)
	job.PostString(BaseUrl & "/login.php", body)
End Sub

Sub JobDone(Job As HttpJob)
	ProgressDialogHide
	If Job.Success Then
		Dim res As String = Job.GetString
		Log("Respuesta login: " & res)
		Dim parser As JSONParser
		parser.Initialize(res)
		Try
			Dim root As Map = parser.NextObject
			If root.ContainsKey("success") And root.Get("success") = True Then
				Dim data As Map = root.Get("data")

				Dim token As String = ""
				If data.ContainsKey("token") Then token = data.Get("token")

				Dim rol As String = "usuario"
				If data.ContainsKey("role") Then rol = data.Get("role")

				Dim sessionMap As Map
				sessionMap.Initialize

				If rol = "usuario" Then
					Dim idUsuario As Int = 0
					Dim nombre As String = ""
					Dim puesto As String = ""
					Dim telefono As String = ""

					If data.ContainsKey("Id_Usuario")     Then idUsuario = data.Get("Id_Usuario")
					If data.ContainsKey("Nombre_Usuario") Then nombre    = data.Get("Nombre_Usuario")
					If data.ContainsKey("Puesto_Usuario") Then puesto    = data.Get("Puesto_Usuario")
					If data.ContainsKey("Telefono_Usuario") Then telefono = data.Get("Telefono_Usuario")

					sessionMap.Put("role",         "usuario")
					sessionMap.Put("Id_Usuario",    idUsuario)
					sessionMap.Put("Nombre_Usuario", nombre)
					sessionMap.Put("Puesto_Usuario", puesto)
					sessionMap.Put("Telefono_Usuario", telefono)
					sessionMap.Put("token",          token)

					Starter.Is_Maquila    = False
					Starter.Id_Usuario    = idUsuario
					Starter.Nombre_Usuario = nombre
					Starter.Puesto_Usuario = puesto
					Starter.Token         = token

					ToastMessageShow("Bienvenido, " & nombre, False)
					StartActivity(menuprincipal_v2)   ' v2: menú en código puro
					Activity.Finish

				Else If rol = "maquila" Then
					Dim idMaquila As Int = 0
					Dim nombreMaquila As String = ""

					If data.ContainsKey("Id_Maquila")     Then idMaquila     = data.Get("Id_Maquila")
					If data.ContainsKey("Nombre_Maquila") Then nombreMaquila = data.Get("Nombre_Maquila")

					sessionMap.Put("role",          "maquila")
					sessionMap.Put("Id_Maquila",     idMaquila)
					sessionMap.Put("Nombre_Maquila", nombreMaquila)
					sessionMap.Put("token",          token)

					Starter.Is_Maquila    = True
					Starter.Id_Maquila    = idMaquila
					Starter.Nombre_Maquila = nombreMaquila
					Starter.Token         = token

					ToastMessageShow("Bienvenido (maquila), " & nombreMaquila, False)
					StartActivity(menuprincipal_maquilas)
					Activity.Finish

				Else
					ToastMessageShow("Tipo de usuario desconocido.", True)
				End If

			Else
				Dim msg As String = ""
				If root.ContainsKey("message") Then msg = root.Get("message") Else msg = "Error en autenticación"
				ToastMessageShow(msg, True)
			End If
		Catch
			ToastMessageShow("Respuesta inválida del servidor", True)
		End Try
	Else
		ToastMessageShow("Error de red: " & Job.ErrorMessage, True)
	End If
	Job.Release
End Sub
