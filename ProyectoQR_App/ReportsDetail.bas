B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.1
@EndOfDesignText@
' ReportDetail.bas
' Activity que muestra el contenido de un reporte (area + car_reports)
' Requisitos: JSON

#Region  Activity Attributes
    #FullScreen: False
    #IncludeTitle: True
#End Region

Sub Process_Globals
	Public ReportJson As String ' opcional: si se asigna externamente
End Sub

Sub Globals
	Private sv As ScrollView
	Private pnl As Panel
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.RemoveAllViews
	Activity.Title = "Detalle reporte"

	sv.Initialize(100%x, 100%y)
	Activity.AddView(sv, 0, 0, 100%x, 100%y)
	pnl = sv.Panel
	pnl.RemoveAllViews

	Dim jsonToUse As String = ReportJson
	If jsonToUse = "" Then
		' intentar leer archivo temporal
		Try
			If File.Exists(File.DirInternal, "current_report.json") Then
				jsonToUse = File.ReadString(File.DirInternal, "current_report.json")
			End If
		Catch
			Log("Error reading current_report.json: " & LastException.Message)
		End Try
	End If

	If jsonToUse = "" Then
		Dim lbl As Label
		lbl.Initialize("lbl")
		lbl.Text = "No hay datos para mostrar."
		lbl.TextSize = 16
		pnl.AddView(lbl, 10dip, 10dip, 100%x - 20dip, 30dip)
		Return
	End If

	Dim parser As JSONParser
	parser.Initialize(jsonToUse)
	Try
		Dim root As Object = parser.NextValue
		If root Is Map Then
			Dim rp As Map = root
			RenderReport(rp)
		Else
			Dim lbl As Label
			lbl.Initialize("lbl2")
			lbl.Text = "Formato de reporte inesperado."
			pnl.AddView(lbl, 10dip, 10dip, 100%x - 20dip, 30dip)
		End If
	Catch
		Dim lblErr As Label
		lblErr.Initialize("lblErr")
		lblErr.Text = "Error parseando reporte: " & LastException.Message
		pnl.AddView(lblErr, 10dip, 10dip, 100%x - 20dip, 40dip)
		Log("Error parseando ReportDetail JSON: " & LastException.Message)
	End Try
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

' Renderiza el mapa del reporte
Sub RenderReport(rp As Map)
	Dim y As Int = 10dip
	Dim padding As Int = 10dip
	Dim w As Int = 100%x - 2 * padding

	' Area info
	If rp.ContainsKey("area") Then
		Try
			Dim area As Map = rp.Get("area")
			Dim lblTitle As Label
			lblTitle.Initialize("lblTitle")
			Dim name As String = ""
			If area.ContainsKey("area_name") Then name = area.Get("area_name")
			If name = "" And area.ContainsKey("areaName") Then name = area.Get("areaName")
			lblTitle.Text = "Área: " & name
			lblTitle.TextSize = 16
			lblTitle.Typeface = Typeface.DEFAULT_BOLD
			pnl.AddView(lblTitle, padding, y, w, 28dip)
			y = y + 28dip + 6dip

			Dim desc As String = ""
			If area.ContainsKey("area_description") Then desc = area.Get("area_description")
			If desc = "" And area.ContainsKey("areaDescription") Then desc = area.Get("areaDescription")
			If desc <> "" Then
				Dim lblDesc As Label
				lblDesc.Initialize("lblDesc")
				lblDesc.Text = "Descripción: " & desc
				lblDesc.TextSize = 14
				lblDesc.SingleLine = False
				lblDesc.Autosize = True
				pnl.AddView(lblDesc, padding, y, w, 60dip)
				y = y + lblDesc.Height + 6dip
			End If
		Catch
			Log("Error leyendo campo area: " & LastException.Message)
		End Try
	End If

	' CAR reports
	If rp.ContainsKey("car_reports") Then
		Try
			Dim cars As List = rp.Get("car_reports")
			For i = 0 To cars.Size - 1
				Dim c As Map = cars.Get(i)
				Dim carName As String = ""
				If c.ContainsKey("car_name") Then carName = c.Get("car_name")

				' Card panel
				Dim card As Panel
				card.Initialize("card" & i)
				card.Color = Colors.ARGB(255, 245, 245, 245)
				pnl.AddView(card, padding, y, w, 60dip)
				' Car name
				Dim lblCar As Label
				lblCar.Initialize("lblCar" & i)
				lblCar.Text = "CAR: " & carName
				lblCar.TextSize = 15
				lblCar.Typeface = Typeface.DEFAULT_BOLD
				card.AddView(lblCar, 8dip, 4dip, w - 16dip, 22dip)

				' Responses (formatted)
				Dim responsesText As String = ""
				If c.ContainsKey("responses") Then
					Try
						Dim respMap As Map = c.Get("responses")
						Dim keys As List = respMap.Keys
						For Each k As String In keys
							Dim v As Object = respMap.Get(k)
							responsesText = responsesText & k & ": " & v & CRLF
						Next
					Catch
						responsesText = "Error leyendo responses"
						Log("Error reading responses map: " & LastException.Message)
					End Try
				End If

				' Observacion / incidencia
				Dim extras As String = ""
				If c.ContainsKey("observacion") Then extras = extras & "Observación: " & c.Get("observacion") & CRLF
				If c.ContainsKey("incidencia") Then extras = extras & "Incidencia: " & c.Get("incidencia") & CRLF

				Dim lblResp As Label
				lblResp.Initialize("lblResp" & i)
				Dim details As String = ""
				If responsesText <> "" Then details = details & responsesText
				If extras <> "" Then details = details & extras
				If details = "" Then details = "(sin detalles)"
				lblResp.Text = details
				lblResp.SingleLine = False
				lblResp.Autosize = True
				lblResp.TextSize = 13
				card.AddView(lblResp, 8dip, 28dip, w - 16dip, 28dip)

				' ajustar altura del card según contenido (aprox)
				Dim hCard As Int = lblResp.Height + 36dip
				card.SetLayout(card.Left, card.Top, card.Width, hCard)
				y = y + hCard + 8dip
			Next
		Catch
			Log("Error leyendo car_reports: " & LastException.Message)
		End Try
	Else
		If rp.ContainsKey("raw") Then
			Dim lblRaw As Label
			lblRaw.Initialize("lblRaw")
			lblRaw.Text = rp.Get("raw")
			lblRaw.SingleLine = False
			lblRaw.Autosize = True
			pnl.AddView(lblRaw, padding, y, w, 120dip)
			y = y + lblRaw.Height + 6dip
		End If
	End If

	pnl.Height = y + 20dip
End Sub
