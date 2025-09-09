B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.1
@EndOfDesignText@
#Region  Activity Attributes
    #FullScreen: False
    #IncludeTitle: False
#End Region

' ==== ReportDialog (versión corregida y simplificada) ====
' - No usa tipo Object.
' - No guarda Views fuera de Globals.
' - If / Else If / Else bien balanceados.
' Requiere: librería JSON marcada.

Sub Process_Globals
	' Vacío a propósito: no colocar aquí Views ni colecciones con Views.
End Sub

Sub Globals
	' ---- UI base ----
	Private pnlMain As Panel
	Private lblTitle As Label
	Private pnlContent As Panel
	Private btnPrev As Button
	Private btnNext As Button
	Private btnSave As Button
	Private btnCancel As Button

	' ---- Datos del área / CARs ----
	Private areaMap As Map
	Private carsList As List          ' Lista de CARs (cada item es Map)
	Private currentIndex As Int       ' índice del CAR mostrado

	' ---- Estructuras de la pantalla actual ----
	Private curEditTexts As List      ' List<EditText> del CAR mostrado
	Private curCheckBoxes As List     ' List<CheckBox> del CAR mostrado
	Private curPropOrder As List      ' List<Map{type,label,idx}> define cómo leer cada control
	Private curObs As EditText        ' observación del CAR mostrado
	Private curInc As EditText        ' incidencia del CAR mostrado

	' ---- Respuestas almacenadas (una entrada por CAR) ----
	' answersList(i) = Map { "car_name":String, "responses":Map, "observacion":String?, "incidencia":String? }
	Private answersList As List
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.RemoveAllViews

	' ---- UI ----
	pnlMain.Initialize("pnlMain")
	Activity.AddView(pnlMain, 0, 0, 100%x, 100%y)
	pnlMain.Color = Colors.White

	lblTitle.Initialize("")
	lblTitle.TextSize = 16
	lblTitle.TextColor = Colors.Black
	pnlMain.AddView(lblTitle, 8dip, 8dip, 84%x, 28dip)

	pnlContent.Initialize("pnlContent")
	pnlContent.Color = Colors.White
	pnlMain.AddView(pnlContent, 0, 44dip, 100%x, 70%y)

	btnPrev.Initialize("btnPrev")
	btnPrev.Text = "Anterior"
	pnlMain.AddView(btnPrev, 6dip, 76%y, 30%x - 12dip, 40dip)

	btnNext.Initialize("btnNext")
	btnNext.Text = "Siguiente"
	pnlMain.AddView(btnNext, 34%x + 6dip, 76%y, 30%x - 12dip, 40dip)

	btnSave.Initialize("btnSave")
	btnSave.Text = "Generar JSON"
	pnlMain.AddView(btnSave, 62%x + 12dip, 76%y, 36%x - 18dip, 40dip)

	btnCancel.Initialize("btnCancel")
	btnCancel.Text = "Cancelar"
	pnlMain.AddView(btnCancel, 6dip, 82%y + 48dip, 88%x, 36dip)

	' ---- Cargar JSON del área guardado por la pantalla anterior ----
	If File.Exists(File.DirInternal, "last_area.json") = False Then
		ToastMessageShow("No hay datos de área (last_area.json). Escanea primero.", True)
		Activity.Finish
		Return
	End If

	Dim raw As String = File.ReadString(File.DirInternal, "last_area.json")
	' Sanea por si viene con prefijo (ej. <script>console.log(...)</script>)
	Dim p As Int = raw.IndexOf("{")
	If p > -1 Then raw = raw.SubString(p)

	Dim jp As JSONParser
	jp.Initialize(raw)
	Dim root As Map
	Try
		root = jp.NextObject
	Catch
		ToastMessageShow("JSON de área inválido.", True)
		Activity.Finish
		Return
	End Try

	If root.ContainsKey("data") Then
		areaMap = root.Get("data")
	Else
		areaMap = root
	End If

	If areaMap.IsInitialized = False Then
		areaMap.Initialize
	End If

	If areaMap.ContainsKey("cars") Then
		carsList = areaMap.Get("cars")
	Else
		carsList.Initialize
	End If

	If carsList.IsInitialized = False Or carsList.Size = 0 Then
		ToastMessageShow("No se encontraron C.A.R. en el área.", True)
		Activity.Finish
		Return
	End If

	' Normaliza: asegura que cada item de carsList sea Map
	Dim i As Int
	For i = 0 To carsList.Size - 1
		If carsList.Get(i) Is Map Then
			' ok
		Else If carsList.Get(i) Is String Then
			Dim s As String = carsList.Get(i)
			Dim jp2 As JSONParser
			jp2.Initialize(s)
			Dim m As Map
			Try
				m = jp2.NextObject
			Catch
				m.Initialize
			End Try
			carsList.Set(i, m)
		Else
			Dim empty As Map
			empty.Initialize
			carsList.Set(i, empty)
		End If
	Next

	' Prepara almacenamiento de respuestas (Map vacío por CAR)
	answersList.Initialize
	For i = 0 To carsList.Size - 1
		Dim tmp As Map
		tmp.Initialize
		answersList.Add(tmp)
	Next

	' Inicializa colecciones de la pantalla actual
	curEditTexts.Initialize
	curCheckBoxes.Initialize
	curPropOrder.Initialize
	currentIndex = 0
	ShowCAR(currentIndex, True)
End Sub

' Muestra un CAR por índice. Si isFirst=False, antes guarda lo que esté en pantalla.
Private Sub ShowCAR(index As Int, isFirst As Boolean)
	If isFirst = False Then
		SaveCurrentValues
	End If

	pnlContent.RemoveAllViews
	curEditTexts.Initialize
	curCheckBoxes.Initialize
	curPropOrder.Initialize

	If index < 0 Or index > carsList.Size - 1 Then Return

	Dim carMap As Map = carsList.Get(index)
	Dim carName As String = GetFirstString(carMap, Array As String("name","nombre","Nombre"))
	If carName = "" Then carName = "C.A.R. " & (index + 1)
	lblTitle.Text = "Reportar: " & carName & "  (" & (index + 1) & " de " & carsList.Size & ")"

	' Recupera respuestas previas (si las hay) para prellenar
	Dim saved As Map = answersList.Get(index)
	Dim savedResp As Map
	savedResp.Initialize
	If saved.IsInitialized And saved.ContainsKey("responses") Then
		savedResp = saved.Get("responses")
	End If
	Dim savedObs As String = ""
	Dim savedInc As String = ""
	If saved.IsInitialized And saved.ContainsKey("observacion") Then savedObs = saved.Get("observacion")
	If saved.IsInitialized And saved.ContainsKey("incidencia") Then savedInc = saved.Get("incidencia")

	' Construye controles según properties
	Dim top As Int = 8dip
	Dim props As List
	If carMap.ContainsKey("properties") Then
		props = carMap.Get("properties")
	Else
		props.Initialize
	End If

	Dim j As Int
	For j = 0 To props.Size - 1
		Dim prop As Map
		If props.Get(j) Is Map Then
			prop = props.Get(j)
		Else
			Dim mp As Map
			mp.Initialize
			prop = mp
		End If

		Dim label As String = GetFirstString(prop, Array As String("label","name","key"))
		If label = "" Then label = "prop_" & j

		Dim ptype As String = GetFirstString(prop, Array As String("type","tipo")).ToLowerCase

		' etiqueta
		Dim lbl As Label
		lbl.Initialize("")
		lbl.Text = label
		lbl.TextSize = 12
		lbl.TextColor = Colors.Black
		pnlContent.AddView(lbl, 8dip, top, 84%x, 22dip)

		If ptype = "bool" Or ptype = "boolean" Then
			Dim cb As CheckBox
			cb.Initialize("")
			Dim dflt As Boolean = False
			If prop.ContainsKey("default") Then
				Try
					dflt = prop.Get("default")
				Catch
					dflt = False
				End Try
			End If
			' si había guardado valor, úsalo
			If savedResp.ContainsKey(label) Then
				Try
					dflt = savedResp.Get(label)
				Catch
				End Try
			End If
			cb.Checked = dflt
			pnlContent.AddView(cb, 8dip, top + 24dip, 24dip, 24dip)

			curCheckBoxes.Add(cb)
			Dim metaCb As Map
			metaCb.Initialize
			metaCb.Put("type", "checkbox")
			metaCb.Put("label", label)
			metaCb.Put("idx", curCheckBoxes.Size - 1)
			curPropOrder.Add(metaCb)

			top = top + 24dip + 16dip

		Else If ptype = "number" Or ptype = "integer" Or ptype = "float" Then
			Dim etN As EditText
			etN.Initialize("")
			etN.SingleLine = True
			Dim hint As String = ""
			If prop.ContainsKey("min") Then hint = "min:" & prop.Get("min")
			If prop.ContainsKey("max") Then
				If hint.Length > 0 Then hint = hint & " "
				hint = hint & "max:" & prop.Get("max")
			End If
			If prop.ContainsKey("step") Then
				If hint.Length > 0 Then hint = hint & " "
				hint = hint & "step:" & prop.Get("step")
			End If
			etN.Hint = hint

			Dim txt As String = ""
			If prop.ContainsKey("default") Then txt = prop.Get("default")
			If savedResp.ContainsKey(label) Then
				Try
					txt = savedResp.Get(label)
				Catch
				End Try
			End If
			etN.Text = txt

			pnlContent.AddView(etN, 8dip, top + 22dip, 84%x, 34dip)

			curEditTexts.Add(etN)
			Dim metaN As Map
			metaN.Initialize
			metaN.Put("type", "edittext")
			metaN.Put("label", label)
			metaN.Put("idx", curEditTexts.Size - 1)
			curPropOrder.Add(metaN)

			top = top + 22dip + 34dip + 8dip

		Else
			' texto
			Dim etT As EditText
			etT.Initialize("")
			Dim placeholder As String = ""
			If prop.ContainsKey("placeholder") Then placeholder = prop.Get("placeholder")
			etT.Hint = placeholder
			etT.SingleLine = False

			Dim txt2 As String = ""
			If prop.ContainsKey("default") Then txt2 = prop.Get("default")
			If savedResp.ContainsKey(label) Then
				Try
					txt2 = savedResp.Get(label)
				Catch
				End Try
			End If
			etT.Text = txt2

			pnlContent.AddView(etT, 8dip, top + 22dip, 84%x, 72dip)

			curEditTexts.Add(etT)
			Dim metaT As Map
			metaT.Initialize
			metaT.Put("type", "edittext")
			metaT.Put("label", label)
			metaT.Put("idx", curEditTexts.Size - 1)
			curPropOrder.Add(metaT)

			top = top + 22dip + 72dip + 8dip
		End If
	Next

	' Observación
	Dim lblObs As Label
	lblObs.Initialize("")
	lblObs.Text = "Observación"
	lblObs.TextSize = 12
	pnlContent.AddView(lblObs, 8dip, top, 84%x, 20dip)

	curObs.Initialize("")
	curObs.SingleLine = False
	curObs.Text = savedObs
	pnlContent.AddView(curObs, 8dip, top + 20dip, 84%x, 70dip)
	top = top + 20dip + 70dip + 8dip

	' Incidencia
	Dim lblInc As Label
	lblInc.Initialize("")
	lblInc.Text = "Incidencia"
	lblInc.TextSize = 12
	pnlContent.AddView(lblInc, 8dip, top, 84%x, 20dip)

	curInc.Initialize("")
	curInc.SingleLine = False
	curInc.Text = savedInc
	pnlContent.AddView(curInc, 8dip, top + 20dip, 84%x, 70dip)

	' Navegación
	btnPrev.Enabled = (index > 0)
	btnNext.Enabled = (index < carsList.Size - 1)
	currentIndex = index
End Sub

' Guarda lo que está actualmente en pantalla hacia answersList(currentIndex)
Private Sub SaveCurrentValues
	If curPropOrder.IsInitialized = False Then Return

	Dim responses As Map
	responses.Initialize

	Dim k As Int
	For k = 0 To curPropOrder.Size - 1
		Dim meta As Map = curPropOrder.Get(k)
		Dim t As String = meta.Get("type")
		Dim label As String = meta.Get("label")
		Dim idx As Int = meta.Get("idx")

		If t = "edittext" Then
			Dim et As EditText = curEditTexts.Get(idx)
			Dim v As String = et.Text
			If v <> "" Then
				responses.Put(label, v)
			End If
		Else If t = "checkbox" Then
			Dim cb As CheckBox = curCheckBoxes.Get(idx)
			responses.Put(label, cb.Checked)
		End If
	Next

	Dim store As Map
	store.Initialize
	Dim carMap As Map = carsList.Get(currentIndex)
	store.Put("car_name", GetFirstString(carMap, Array As String("name","nombre","Nombre")))
	store.Put("responses", responses)
	If curObs.IsInitialized And curObs.Text <> "" Then store.Put("observacion", curObs.Text)
	If curInc.IsInitialized And curInc.Text <> "" Then store.Put("incidencia", curInc.Text)

	answersList.Set(currentIndex, store)
End Sub

Private Sub btnPrev_Click
	If currentIndex > 0 Then
		ShowCAR(currentIndex - 1, False)
	End If
End Sub

Private Sub btnNext_Click
	If currentIndex < carsList.Size - 1 Then
		ShowCAR(currentIndex + 1, False)
	End If
End Sub

Private Sub btnCancel_Click
	Activity.Finish
End Sub

Private Sub btnSave_Click
	' Guarda lo actual antes de generar
	SaveCurrentValues

	Dim report As Map
	report.Initialize

	Dim areaSummary As Map
	areaSummary.Initialize
	areaSummary.Put("area_name", GetFirstString(areaMap, Array As String("area_name","Nombre_Area","name")))
	areaSummary.Put("area_description", GetFirstString(areaMap, Array As String("area_description","Descripcion_Area","description")))
	report.Put("area", areaSummary)

	Dim carReports As List
	carReports.Initialize

	Dim i As Int
	For i = 0 To answersList.Size - 1
		If answersList.Get(i) Is Map Then
			Dim m As Map = answersList.Get(i)
			If m.IsInitialized And m.Size > 0 Then
				carReports.Add(m)
			Else
				' Si el usuario no llenó nada, aun así empuja el nombre del CAR
				Dim fallback As Map
				fallback.Initialize
				Dim carMap As Map = carsList.Get(i)
				fallback.Put("car_name", GetFirstString(carMap, Array As String("name","nombre","Nombre")))
				fallback.Put("responses", CreateMap())
				carReports.Add(fallback)
			End If
		Else
			Dim fallback2 As Map
			fallback2.Initialize
			Dim carMap2 As Map = carsList.Get(i)
			fallback2.Put("car_name", GetFirstString(carMap2, Array As String("name","nombre","Nombre")))
			fallback2.Put("responses", CreateMap())
			carReports.Add(fallback2)
		End If
	Next

	report.Put("car_reports", carReports)

	Dim jg As JSONGenerator
	jg.Initialize(report)
	Dim reportJson As String = jg.ToString

	File.WriteString(File.DirInternal, "report.json", reportJson)
	MsgboxAsync("Reporte generado y guardado en report.json", "OK")
	Activity.Finish
End Sub

' ---- Helper: devuelve el primer valor string disponible entre varias claves ----
Private Sub GetFirstString(data As Map, keys() As String) As String
	If data.IsInitialized = False Then Return ""
	Dim i As Int
	For i = 0 To keys.Length - 1
		Dim k As String = keys(i)
		If data.ContainsKey(k) Then
			If data.Get(k) <> Null Then
				Return data.Get(k)
			End If
		End If
	Next
	Return ""
End Sub
