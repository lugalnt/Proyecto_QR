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
	'Do not forget to load the layout file created with the visual designer. For example:
	'Activity.LoadLayout("Layout1")
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
				' Extraer valores
				Dim token As String = data.Get("token")
				Dim idUsuario As Int = data.Get("Id_Usuario")
				Dim nombre As String = data.Get("Nombre_Usuario")
				Dim puesto As String = data.Get("Puesto_Usuario")

				' Guardar sesión como JSON válido usando JSONGenerator
				Dim sessionMap As Map
				sessionMap.Initialize
				sessionMap.Put("Id_Usuario", idUsuario)
				sessionMap.Put("Nombre_Usuario", nombre)
				sessionMap.Put("Telefono_Usuario", data.Get("Telefono_Usuario"))
				sessionMap.Put("Puesto_Usuario", data.Get("Puesto_Usuario"))
				sessionMap.Put("token", token)

				Dim jg As JSONGenerator
				jg.Initialize(sessionMap)

				ToastMessageShow("Bienvenido, " & nombre, False)
				' StartActivity(Main)  ' Descomenta si quieres abrir Main
				
				Starter.Id_Usuario = idUsuario
				Starter.Nombre_Usuario = nombre
				Starter.Puesto_Usuario = puesto
				
				StartActivity(menuprincipal)
				Activity.Finish
				
			Else
				Dim msg As String = root.Get("message")
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