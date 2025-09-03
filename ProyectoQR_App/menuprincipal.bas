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
	Dim BaseUrl As String = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api"
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	Dim Nombre_Usuario As String = Starter.Nombre_Usuario
	
	Private Scanner As GoogleCodeScanner
	Private LB_Bienvenido_NombreUsuario As Label
	Private ET_DatosArea As EditText
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	'Activity.LoadLayout("Layout1")
	Activity.LoadLayout("menuprincipal")
	LB_Bienvenido_NombreUsuario.Text = Nombre_Usuario
	Scanner.Initialize

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


Private Sub BTN_IniciarReporte_Click

	
End Sub




Sub JobDone(Job As HttpJob)
	ProgressDialogHide
	If Job.Success Then
		If Job.JobName = "JobArea" Then
			Dim res As String = Job.GetString
			Log("Respuesta API getArea: " & res)

			' --- Limpiar respuesta por si el servidor manda algo extra (scripts) ---
			Dim startIndex As Int = res.IndexOf("{")
			If startIndex > -1 Then res = res.SubString(startIndex)

			' --- Parsear JSON ---
			Dim parser As JSONParser
			parser.Initialize(res)
			Try
				Dim root As Map = parser.NextObject
				If root.ContainsKey("success") And root.Get("success") = True Then
					Dim data As Map = root.Get("data")

					' Obtener Nombre del área (probar varias claves posibles)
					Dim nombre As String = ""
					If data.ContainsKey("Nombre_Area") Then
						nombre = data.Get("Nombre_Area")
					Else If data.ContainsKey("Nombre") Then
						nombre = data.Get("Nombre")
					Else If data.ContainsKey("NombreArea") Then
						nombre = data.Get("NombreArea")
					End If

					' Obtener Descripción del área
					Dim descripcion As String = ""
					If data.ContainsKey("Descripcion_Area") Then
						descripcion = data.Get("Descripcion_Area")
					Else If data.ContainsKey("Descripcion") Then
						descripcion = data.Get("Descripcion")
					Else If data.ContainsKey("DescripcionArea") Then
						descripcion = data.Get("DescripcionArea")
					End If

					' Obtener Número CAR del área
					Dim numeroCAR As String = ""
					If data.ContainsKey("NumeroCAR_Area") Then
						numeroCAR = data.Get("NumeroCAR_Area")
					Else If data.ContainsKey("NumeroCAR") Then
						numeroCAR = data.Get("NumeroCAR")
					Else If data.ContainsKey("Numero_CAR") Then
						numeroCAR = data.Get("Numero_CAR")
					End If

					' Construir texto: solo Nombre, Descripción, Número CAR (cada uno en nueva línea)
					Dim texto As String = "Nombre del Area: " & nombre & CRLF & "Descripción: " & descripcion & CRLF & "Numero C.A.R: " & numeroCAR

					ET_DatosArea.Text = texto
				Else
					Dim msg As String = "Área no encontrada"
					If root.ContainsKey("message") Then msg = root.Get("message")
					ToastMessageShow(msg, True)
				End If
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
	' Opcional: indicar al usuario que se está consultando
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
		' En lugar del Msgbox, consultamos al servidor
		ConsultarArea(Result.Value)
	End If
End Sub

