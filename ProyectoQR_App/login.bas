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

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Dim BaseUrl As String = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api" ' <- Cambia a tu URL
	
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private Nombre_Usuario As EditText
	Private Password_Usuario As EditText
	
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("loginscreen")
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub URLEncode(s As String) As String
	Dim jo As JavaObject
	jo.InitializeStatic("java.net.URLEncoder")
	Dim result As String
	result = jo.RunMethod("encode", Array(s, "UTF-8"))
	Return result
End Sub


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
				
				' token (si viene)
				Dim token As String = ""
				If data.ContainsKey("token") Then token = data.Get("token")
				
				' role: "usuario" o "maquila" (si no viene, asumimos usuario)
				Dim rol As String = "usuario"
				If data.ContainsKey("role") Then rol = data.Get("role")
				
				' Map para sesión — lo iremos llenando según rol
				Dim sessionMap As Map
				sessionMap.Initialize
				
				If rol = "usuario" Then
					' Extraer campos de usuario de forma segura
					Dim idUsuario As Int = 0
					Dim nombre As String = ""
					Dim puesto As String = ""
					Dim telefono As String = ""
					
					If data.ContainsKey("Id_Usuario") Then idUsuario = data.Get("Id_Usuario")
					If data.ContainsKey("Nombre_Usuario") Then nombre = data.Get("Nombre_Usuario")
					If data.ContainsKey("Puesto_Usuario") Then puesto = data.Get("Puesto_Usuario")
					If data.ContainsKey("Telefono_Usuario") Then telefono = data.Get("Telefono_Usuario")
					
					' Llenar sessionMap
					sessionMap.Put("role", "usuario")
					sessionMap.Put("Id_Usuario", idUsuario)
					sessionMap.Put("Nombre_Usuario", nombre)
					sessionMap.Put("Puesto_Usuario", puesto)
					sessionMap.Put("Telefono_Usuario", telefono)
					sessionMap.Put("token", token)
					
					' Guardar en Starter (variables globales compartidas)
					Starter.Is_Maquila = False
					Starter.Id_Usuario = idUsuario
					Starter.Nombre_Usuario = nombre
					Starter.Puesto_Usuario = puesto
					Starter.Token = token
					
					' Mensaje al usuario
					ToastMessageShow("Bienvenido, " & nombre, False)
					
					' Abrir menú principal (misma Activity para usuario)
					StartActivity(menuprincipal)
					Activity.Finish
					
				Else If rol = "maquila" Then
					' Extraer campos de maquila de forma segura
					Dim idMaquila As Int = 0
					Dim nombreMaquila As String = ""
					
					If data.ContainsKey("Id_Maquila") Then idMaquila = data.Get("Id_Maquila")
					If data.ContainsKey("Nombre_Maquila") Then nombreMaquila = data.Get("Nombre_Maquila")
					
					' Llenar sessionMap
					sessionMap.Put("role", "maquila")
					sessionMap.Put("Id_Maquila", idMaquila)
					sessionMap.Put("Nombre_Maquila", nombreMaquila)
					sessionMap.Put("token", token)
					
					' Guardar en Starter (variables globales compartidas)
					Starter.Is_Maquila = True
					Starter.Id_Maquila = idMaquila
					Starter.Nombre_Maquila = nombreMaquila
					Starter.Token = token
					
					' Mensaje al usuario (maquila)
					ToastMessageShow("Bienvenido (maquila), " & nombreMaquila, False)
					
					' Abrir menú principal (puedes abrir Activity distinta si la maquila tiene UI propia)
					StartActivity(menuprincipal_maquilas)
					Activity.Finish
					
				Else
					' Rol inesperado: mostrar mensaje genérico
					ToastMessageShow("Tipo de usuario desconocido.", True)
				End If
				
				' Si quieres persistir la sesión en archivo/sharedprefs:
				' Dim jg As JSONGenerator
				' jg.Initialize(sessionMap)
				' File.WriteString(File.DirInternal, "session.json", jg.ToString)
				
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
