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
' DATOS A MANDAR: FECHA DE REGISTRO, ID AREA, ID USUARIO, NUMERO CAR, NUMERO DE CAR REVISADAS, EL JSON

Sub Process_Globals
	' Vacío a propósito: no colocar aquí Views ni colecciones con Views.
	Dim BaseUrl As String = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api"
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

	' ---------------- UI dinámico y limpio ----------------
	' Reemplaza el bloque UI antiguo por este (dentro de Activity_Create)

	' obtener tamaño pantalla relativo
	Dim screenW As Int = 100%x
	Dim screenH As Int = 100%y

	' márgenes y zonas
	Dim outerMargin As Int = 8dip
	Dim headerH As Int = 12%y        ' altura cabecera (título)
	Dim footerH As Int = 18%y        ' altura zona de botones
	Dim contentTop As Int = headerH + outerMargin
	Dim contentHeight As Int = screenH - headerH - footerH - (outerMargin * 2)

	' medidas para botones
	Dim btnHeight As Int = 48dip
	Dim btnGap As Int = 8dip
	Dim btnCount As Int = 3
	Dim btnWidth As Int = (screenW - (outerMargin * 2) - (btnGap * (btnCount - 1))) / btnCount

	' panel principal (fondo)
	pnlMain.Initialize("pnlMain")
	Activity.AddView(pnlMain, 0, 0, screenW, screenH)
	pnlMain.Color = Colors.White

	' título centrado en la cabecera
	lblTitle.Initialize("lblTitle")
	lblTitle.Text = "Área / Reporte"
	lblTitle.TextSize = 18dip
	lblTitle.TextColor = Colors.Black
	lblTitle.Gravity = Gravity.CENTER
	pnlMain.AddView(lblTitle, outerMargin, 4dip, screenW - (outerMargin * 2), headerH - 8dip)

	' panel de contenido (dentro de la pantalla, con padding)
	pnlContent.Initialize("pnlContent")
	pnlContent.Color = Colors.ARGB(255, 250, 250, 250) ' leve contraste
	pnlMain.AddView(pnlContent, outerMargin, contentTop, screenW - (outerMargin * 2), contentHeight)
	' Opcional: agrega sombra/outline si usas un panel custom (no nativo sin librería)

	' Botones principales (fila)
	Dim yBtns As Int = screenH - footerH + outerMargin
	btnPrev.Initialize("btnPrev")
	btnPrev.Text = "Anterior"
	pnlMain.AddView(btnPrev, outerMargin, yBtns, btnWidth, btnHeight)

	btnNext.Initialize("btnNext")
	btnNext.Text = "Siguiente"
	pnlMain.AddView(btnNext, outerMargin + btnWidth + btnGap, yBtns, btnWidth, btnHeight)

	btnSave.Initialize("btnSave")
	btnSave.Text = "Guardar Reporte"
	pnlMain.AddView(btnSave, outerMargin + 2 * (btnWidth + btnGap), yBtns, btnWidth, btnHeight)

	' Botón cancelar (fila inferior, ancho completo con margen)
	btnCancel.Initialize("btnCancel")
	btnCancel.Text = "Cancelar"
	Dim yCancel As Int = yBtns + btnHeight + btnGap
	pnlMain.AddView(btnCancel, outerMargin, yCancel, screenW - (outerMargin * 2), 44dip)

	' Ajustes visuales (opcionales, personaliza colores)
	btnPrev.Color = Colors.LightGray
	btnNext.Color = Colors.LightGray
	btnSave.Color = Colors.RGB(33,150,243) ' azul
	btnSave.TextColor = Colors.White
	btnCancel.Color = Colors.RGB(220, 53, 69) ' rojo suave
	btnCancel.TextColor = Colors.White


	' ------------------------------------------------------


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
	ToastMessageShow("Reporte guardado localmente. Enviando...", False)
	SendReportToServer
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

' ---------------------
' SendReportToServer (Job estilo Login)
' ---------------------
Sub SendReportToServer()
	If File.Exists(File.DirInternal, "report.json") = False Then
		ToastMessageShow("No existe report.json", True)
		Return
	End If

	Dim reportRaw As String = File.ReadString(File.DirInternal, "report.json")

	' contar CARs y revisadas
	Dim jp As JSONParser
	jp.Initialize(reportRaw)
	Dim reportMap As Map
	Try
		reportMap = jp.NextObject
	Catch
		reportMap.Initialize
	End Try

	Dim carReports As List
	If reportMap.IsInitialized And reportMap.ContainsKey("car_reports") Then
		carReports = reportMap.Get("car_reports")
	Else
		carReports.Initialize
	End If

	Dim total As Int = carReports.Size
	Dim revisadas As Int = 0
	Dim i As Int
	For i = 0 To carReports.Size - 1
		If carReports.Get(i) Is Map Then
			Dim cr As Map = carReports.Get(i)
			If cr.IsInitialized And cr.ContainsKey("responses") Then
				Dim resp As Map = cr.Get("responses")
				If resp.IsInitialized And resp.Size > 0 Then revisadas = revisadas + 1
			End If
		End If
	Next

	' --- Obtener Id_Area robustamente ---
	Dim idArea As String = ""
	If File.Exists(File.DirInternal, "last_area.json") Then
		Dim rawA As String = File.ReadString(File.DirInternal, "last_area.json")
		Log("last_area.json raw: " & rawA)

		' SANITIZAR: quitar prefijos antes del primer '{' (igual que hiciste en Activity_Create)
		Dim p As Int = rawA.IndexOf("{")
		If p > -1 Then rawA = rawA.SubString(p)

		Dim jp2 As JSONParser
		jp2.Initialize(rawA)
		Try
			Dim root As Map = jp2.NextObject
			Dim areaMap As Map
			If root.ContainsKey("data") Then
				areaMap = root.Get("data")
			Else
				areaMap = root
			End If

			If areaMap.IsInitialized Then
				If areaMap.ContainsKey("Id_Area") Then
					idArea = areaMap.Get("Id_Area")
				Else If areaMap.ContainsKey("Id") Then
					idArea = areaMap.Get("Id")
				Else If areaMap.ContainsKey("IdArea") Then
					idArea = areaMap.Get("IdArea")
				Else If areaMap.ContainsKey("JSON_Area") Then
					' JSON_Area puede ser string con inner JSON
					Dim innerRaw As String = areaMap.Get("JSON_Area")
					' SANITIZAR innerRaw también
					Dim q As Int = innerRaw.IndexOf("{")
					If q > -1 Then innerRaw = innerRaw.SubString(q)

					Dim jp3 As JSONParser
					jp3.Initialize(innerRaw)
					Try
						Dim inner As Map = jp3.NextObject
						If inner.ContainsKey("Id_Area") Then idArea = inner.Get("Id_Area")
						If idArea = "" And inner.ContainsKey("id") Then idArea = inner.Get("id")
					Catch
						Log("No se pudo parsear JSON_Area interno.")
					End Try
				End If
			End If
		Catch
			Log("Error parseando last_area.json")
		End Try
	Else
		Log("last_area.json no encontrado")
	End If

	' fallback: Starter.Id_Area (si lo seteaste en Starter desde la pantalla anterior)
	If idArea = "" Then
		Try
			idArea = Starter.Id_Area
			Log("Usando Starter.Id_Area como fallback: " & idArea)
		Catch
			idArea = ""
		End Try
	End If

	' --- Id usuario (desde Starter, como en login) ---
	Dim idUsuario As String = ""
	Try
		idUsuario = Starter.Id_Usuario
	Catch
		idUsuario = ""
	End Try
	Log("Starter.Id_Usuario: " & idUsuario)

	' Si faltan ids no enviamos y alertamos (evita 400 del servidor)
	If idArea = "" Or idUsuario = "" Then
		Dim m As String = "Falta: "
		If idArea = "" Then m = m & "Id_Area "
		If idUsuario = "" Then m = m & "Id_Usuario"
		ToastMessageShow(m, True)
		Log("No se enviará payload. idArea='" & idArea & "', idUsuario='" & idUsuario & "'")
		Return
	End If

	' Construir payload con las claves exactas que espera el controller
	DateTime.DateFormat = "yyyy-MM-dd"
	DateTime.TimeFormat = "HH:mm:ss"
	Dim fecha As String = DateTime.Date(DateTime.Now) & " " & DateTime.Time(DateTime.Now)

	Dim payload As Map
	payload.Initialize
	payload.Put("FechaRegistro_Reporte", fecha)
	payload.Put("FechaModificacion_Reporte", fecha)
	payload.Put("CARTotal_Reporte", total)
	payload.Put("CARRevisadas_Reporte", revisadas)
	payload.Put("Estado_Reporte", "COMPLETADO")
	payload.Put("JSON_Reporte", reportRaw)
	payload.Put("Id_Area", idArea)      ' asegúrate que la clave sea exactamente Id_Area
	payload.Put("Id_Usuario", idUsuario)

	' Log del payload final (útil para depuración)
	Dim jgTest As JSONGenerator
	jgTest.Initialize(payload)
	Log("Payload a enviar: " & jgTest.ToString)

	' Enviar
	ProgressDialogShow("Enviando reporte...")
	Dim job As HttpJob
	job.Initialize("SendReportJob", Me)
	Dim serverUrl As String
	serverUrl = BaseUrl & "/reporte.php"
	Dim jg As JSONGenerator
	jg.Initialize(payload)
	Dim body As String = jg.ToString
	job.PostString(serverUrl, body)
End Sub



' Maneja respuestas de HttpJob — agrega/ajusta si ya tenés otro JobDone en la Activity.
Sub JobDone(Job As HttpJob)
	ProgressDialogHide
	If Job.Success Then
		Dim res As String = Job.GetString
		Log("Respuesta SendReport: " & res)
		Dim parser As JSONParser
		parser.Initialize(res)
		Try
			Dim root As Map = parser.NextObject
			If root.ContainsKey("success") And root.Get("success") = True Then
				Dim idRep As Int = 0
				If root.ContainsKey("Id_Reporte") Then idRep = root.Get("Id_Reporte")
				ToastMessageShow("Reporte guardado. Id: " & idRep, True)
				' Opcional: cerrar activity si querés
				Activity.Finish
			Else
				Dim msg As String = root.GetDefault("error", root.GetDefault("message", "Error desconocido"))
				ToastMessageShow("Error servidor: " & msg, True)
			End If
		Catch
			ToastMessageShow("Respuesta inválida del servidor", True)
		End Try
	Else
		ToastMessageShow("Error de red: " & Job.ErrorMessage, True)
	End If
	Job.Release
End Sub

