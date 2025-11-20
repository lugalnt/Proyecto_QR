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
	Dim Nombre_Maquila As String = Starter.Nombre_Maquila
	' Nota: ya no usamos JsonArea ni guardamos last_area.json aquí.
	
	Private Scanner As GoogleCodeScanner
	Private LB_Bienvenido_NombreUsuario As Label
	Private ET_DatosArea As EditText
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("menuprincipal_maquilas")
	LB_Bienvenido_NombreUsuario.Text = Nombre_Maquila
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


' Ahora BTN_IniciarReporte sólo usa la Id/Código que ya debemos tener en Starter.Id_Area.
Private Sub BTN_IniciarReporte_Click
	Dim areaCode As String = ""
	Try
		' Starter.Id_Area es Int; consideramos válido si es mayor que 0
		Dim idArea As Int = Starter.Id_Area
		If idArea > 0 Then
			areaCode = idArea & ""   ' convertimos a string al asignar a ReportsByArea
		End If
	Catch
		Log("Error leyendo Starter.Id_Area: " & LastException.Message)
		areaCode = ""
	End Try

	If areaCode <> "" Then
		' Llamar ReportsByArea pasando únicamente el código/id del área (como string)
		ReportsByArea.AreaToShow = areaCode
		ReportsByArea.BaseUrlToUse = BaseUrl & "/get_reports_by_area.php?area={area}"
		StartActivity(ReportsByArea)
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

			' ---- limpiar prefijos hasta primer { (evita scripts delante del JSON) ----
			Dim startIndex As Int
			startIndex = res.IndexOf("{")
			If startIndex > -1 Then
				res = res.SubString(startIndex)
			End If

			' ---- parsear el JSON principal ----
			Dim parser As JSONParser
			parser.Initialize(res)
			Try
				Dim root As Map
				root = parser.NextObject

				' Obtener "data" correctamente (puede venir dentro de success.data o ser el mismo root)
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

				' ---- extraer campos (con nombres alternativos) ----
				Dim nombre As String
				nombre = ""
				If data.ContainsKey("Nombre_Area") Then
					nombre = data.Get("Nombre_Area")
				Else If data.ContainsKey("Nombre") Then
					nombre = data.Get("Nombre")
				Else If data.ContainsKey("NombreArea") Then
					nombre = data.Get("NombreArea")
				End If
				
				' Guardar Id_Area en Starter para que BTN_IniciarReporte lo use después
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

				' -- Eliminado: no generamos ni guardamos JSON_Area ni escribimos last_area.json --
				' Si antes dependían otros módulos de ese archivo, avísame y lo adaptamos, pero por
				' ahora lo quitamos tal como pediste.

				' ---- mostrar en pantalla los campos solicitados ----
				Dim texto As String
				texto = "Nombre del Area: " & nombre & CRLF
				texto = texto & "Descripción: " & descripcion & CRLF
				texto = texto & "Numero C.A.R: " & numeroCAR
				ET_DatosArea.Text = texto

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
