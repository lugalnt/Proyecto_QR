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

	' Inicializar ScrollView con un parámetro (altura). Esto evita errores de sintaxis.
	sv.Initialize(100%y)
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
		pnl.AddView(lbl, 10dip, 10dip, 100%x - 20dip, EstimateLabelHeight(lbl.Text, lbl.TextSize, 100%x - 20dip))
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
			pnl.AddView(lbl, 10dip, 10dip, 100%x - 20dip, EstimateLabelHeight(lbl.Text, lbl.TextSize, 100%x - 20dip))
		End If
	Catch
		Dim lblErr As Label
		lblErr.Initialize("lblErr")
		lblErr.Text = "Error parseando reporte: " & LastException.Message
		pnl.AddView(lblErr, 10dip, 10dip, 100%x - 20dip, EstimateLabelHeight(lblErr.Text, lblErr.TextSize, 100%x - 20dip))
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
			Dim hTitle As Int = EstimateLabelHeight(lblTitle.Text, lblTitle.TextSize, w)
			pnl.AddView(lblTitle, padding, y, w, hTitle)
			y = y + hTitle + 6dip

			Dim desc As String = ""
			If area.ContainsKey("area_description") Then desc = area.Get("area_description")
			If desc = "" And area.ContainsKey("areaDescription") Then desc = area.Get("areaDescription")
			If desc <> "" Then
				Dim lblDesc As Label
				lblDesc.Initialize("lblDesc")
				lblDesc.Text = "Descripción: " & desc
				lblDesc.TextSize = 14
				Dim hDesc As Int = EstimateLabelHeight(lblDesc.Text, lblDesc.TextSize, w)
				pnl.AddView(lblDesc, padding, y, w, hDesc)
				y = y + hDesc + 6dip
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
				pnl.AddView(card, padding, y, w, 60dip) ' altura provisional; la ajustaremos luego

				' Car name
				Dim lblCar As Label
				lblCar.Initialize("lblCar" & i)
				lblCar.Text = "CAR: " & carName
				lblCar.TextSize = 15
				lblCar.Typeface = Typeface.DEFAULT_BOLD
				Dim hCarName As Int = EstimateLabelHeight(lblCar.Text, lblCar.TextSize, w - 16dip)
				card.AddView(lblCar, 8dip, 4dip, w - 16dip, hCarName)

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

				Dim details As String = ""
				If responsesText <> "" Then details = details & responsesText
				If extras <> "" Then details = details & extras
				If details = "" Then details = "(sin detalles)"

				Dim lblResp As Label
				lblResp.Initialize("lblResp" & i)
				lblResp.Text = details
				lblResp.TextSize = 13
				Dim hResp As Int = EstimateLabelHeight(lblResp.Text, lblResp.TextSize, w - 16dip)

				card.AddView(lblResp, 8dip, 4dip + hCarName, w - 16dip, hResp)

				' ajustar altura del card según contenido
				Dim hCard As Int = hCarName + hResp + 12dip
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
			lblRaw.TextSize = 13
			Dim hRaw As Int = EstimateLabelHeight(lblRaw.Text, lblRaw.TextSize, w)
			pnl.AddView(lblRaw, padding, y, w, hRaw)
			y = y + hRaw + 6dip
		End If
	End If

	pnl.Height = y + 20dip
End Sub

' Estima una altura adecuada (en px/dip) para un Label con texto multilínea.
' Esto es una aproximación: divide el ancho entre un ancho medio de carácter
' y calcula el número de líneas, luego devuelve líneas * lineHeight.
Sub EstimateLabelHeight(text As String, textSize As Int, width As Int) As Int
	If text = "" Then Return 30dip
	If width <= 0 Then width = 100dip
	' estimación: ancho medio de carácter aproximado = textSize * 0.6
	Dim charWidth As Int = (textSize * 6) / 10
	If charWidth < 4 Then charWidth = 4 ' mínimo razonable
	Dim charsPerLine As Int = width / charWidth
	If charsPerLine < 1 Then charsPerLine = 1
	Dim textLen As Int = text.Length
	Dim lines As Int = textLen / charsPerLine
	If (textLen Mod charsPerLine) <> 0 Then lines = lines + 1
	' altura por línea aproximada: 1.6 * textSize
	Dim lineHeight As Int = (textSize * 16) / 10
	If lineHeight < 14 Then lineHeight = 14
	Dim result As Int = lines * lineHeight + 6dip
	Return result
End Sub
