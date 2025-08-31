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


End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	Dim Nombre_Usuario As String = Starter.Nombre_Usuario
	
	Private Scanner As GoogleCodeScanner
	Private LB_Bienvenido_NombreUsuario As Label
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


Private Sub BTN_IniciarReporte_Click
	'Aqui logica de camara para escanear QR y ver si podemos 
	
End Sub

Private Sub BTN_EscanearArea_Click
	Dim formats As List = Array(Scanner.FORMAT_ALL_FORMATS)
	Wait For (Scanner.Scan(formats)) Complete (Result As ScannerResult)
	If Result.Success Then
		MsgboxAsync(Result.Value, "Escaneado")
	End If
End Sub