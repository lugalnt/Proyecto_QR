package b4a.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class menuprincipal_maquilas extends Activity implements B4AActivity{
	public static menuprincipal_maquilas mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.menuprincipal_maquilas");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (menuprincipal_maquilas).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.menuprincipal_maquilas");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.menuprincipal_maquilas", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (menuprincipal_maquilas) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (menuprincipal_maquilas) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return menuprincipal_maquilas.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (menuprincipal_maquilas) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (menuprincipal_maquilas) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            menuprincipal_maquilas mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (menuprincipal_maquilas) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static String _baseurl = "";
public static String _nombre_maquila = "";
public b4a.example.googlecodescanner _scanner = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb_bienvenido_nombreusuario = null;
public anywheresoftware.b4a.objects.EditTextWrapper _et_datosarea = null;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.menuprincipal _menuprincipal = null;
public b4a.example.reportdialog _reportdialog = null;
public b4a.example.login _login = null;
public b4a.example.reportsbyarea _reportsbyarea = null;
public b4a.example.reportdetail _reportdetail = null;
public b4a.example.httputils2service _httputils2service = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 23;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 24;BA.debugLine="Activity.LoadLayout(\"menuprincipal_maquilas\")";
mostCurrent._activity.LoadLayout("menuprincipal_maquilas",mostCurrent.activityBA);
 //BA.debugLineNum = 25;BA.debugLine="LB_Bienvenido_NombreUsuario.Text = Nombre_Maquila";
mostCurrent._lb_bienvenido_nombreusuario.setText(BA.ObjectToCharSequence(mostCurrent._nombre_maquila));
 //BA.debugLineNum = 26;BA.debugLine="Scanner.Initialize";
mostCurrent._scanner._initialize /*String*/ (processBA);
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 33;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}
public static void  _btn_escaneararea_click() throws Exception{
ResumableSub_BTN_EscanearArea_Click rsub = new ResumableSub_BTN_EscanearArea_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_BTN_EscanearArea_Click extends BA.ResumableSub {
public ResumableSub_BTN_EscanearArea_Click(b4a.example.menuprincipal_maquilas parent) {
this.parent = parent;
}
b4a.example.menuprincipal_maquilas parent;
anywheresoftware.b4a.objects.collections.List _formats = null;
b4a.example.googlecodescanner._scannerresult _result = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 187;BA.debugLine="Dim formats As List = Array(Scanner.FORMAT_ALL_FO";
_formats = new anywheresoftware.b4a.objects.collections.List();
_formats = anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._scanner._format_all_formats /*int*/ )});
 //BA.debugLineNum = 188;BA.debugLine="Wait For (Scanner.Scan(formats)) Complete (Result";
anywheresoftware.b4a.keywords.Common.WaitFor("complete", processBA, this, parent.mostCurrent._scanner._scan /*anywheresoftware.b4a.keywords.Common.ResumableSubWrapper*/ (_formats));
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (b4a.example.googlecodescanner._scannerresult) result[0];
;
 //BA.debugLineNum = 189;BA.debugLine="If Result.Success Then";
if (true) break;

case 1:
//if
this.state = 4;
if (_result.Success /*boolean*/ ) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 191;BA.debugLine="ConsultarArea(Result.Value)";
_consultararea(_result.Value /*String*/ );
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 193;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _complete(b4a.example.googlecodescanner._scannerresult _result) throws Exception{
}
public static String  _btn_iniciarreporte_click() throws Exception{
String _areacode = "";
int _idarea = 0;
 //BA.debugLineNum = 47;BA.debugLine="Private Sub BTN_IniciarReporte_Click";
 //BA.debugLineNum = 48;BA.debugLine="Dim areaCode As String = \"\"";
_areacode = "";
 //BA.debugLineNum = 49;BA.debugLine="Try";
try { //BA.debugLineNum = 51;BA.debugLine="Dim idArea As Int = Starter.Id_Area";
_idarea = mostCurrent._starter._id_area /*int*/ ;
 //BA.debugLineNum = 52;BA.debugLine="If idArea > 0 Then";
if (_idarea>0) { 
 //BA.debugLineNum = 53;BA.debugLine="areaCode = idArea & \"\"   ' convertimos a string";
_areacode = BA.NumberToString(_idarea)+"";
 };
 } 
       catch (Exception e8) {
			processBA.setLastException(e8); //BA.debugLineNum = 56;BA.debugLine="Log(\"Error leyendo Starter.Id_Area: \" & LastExce";
anywheresoftware.b4a.keywords.Common.LogImpl("73276809","Error leyendo Starter.Id_Area: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 57;BA.debugLine="areaCode = \"\"";
_areacode = "";
 };
 //BA.debugLineNum = 60;BA.debugLine="If areaCode <> \"\" Then";
if ((_areacode).equals("") == false) { 
 //BA.debugLineNum = 62;BA.debugLine="ReportsByArea.AreaToShow = areaCode";
mostCurrent._reportsbyarea._areatoshow /*String*/  = _areacode;
 //BA.debugLineNum = 63;BA.debugLine="ReportsByArea.BaseUrlToUse = BaseUrl & \"/get_rep";
mostCurrent._reportsbyarea._baseurltouse /*String*/  = _baseurl+"/get_reports_by_area.php?area={area}";
 //BA.debugLineNum = 64;BA.debugLine="StartActivity(ReportsByArea)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._reportsbyarea.getObject()));
 }else {
 //BA.debugLineNum = 66;BA.debugLine="MsgboxAsync(\"Escanea un área primero\", \"Reporte\"";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Escanea un área primero"),BA.ObjectToCharSequence("Reporte"),processBA);
 };
 //BA.debugLineNum = 68;BA.debugLine="End Sub";
return "";
}
public static String  _consultararea(String _codigo) throws Exception{
b4a.example.httpjob _job = null;
String _body = "";
 //BA.debugLineNum = 175;BA.debugLine="Sub ConsultarArea(codigo As String)";
 //BA.debugLineNum = 177;BA.debugLine="ProgressDialogShow(\"Buscando área...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Buscando área..."));
 //BA.debugLineNum = 178;BA.debugLine="Dim job As HttpJob";
_job = new b4a.example.httpjob();
 //BA.debugLineNum = 179;BA.debugLine="job.Initialize(\"JobArea\", Me)";
_job._initialize /*String*/ (processBA,"JobArea",menuprincipal_maquilas.getObject());
 //BA.debugLineNum = 180;BA.debugLine="Dim body As String";
_body = "";
 //BA.debugLineNum = 181;BA.debugLine="body = \"codigo=\" & URLEncode(codigo)";
_body = "codigo="+_urlencode(_codigo);
 //BA.debugLineNum = 182;BA.debugLine="job.PostString(BaseUrl & \"/getArea.php\", body)";
_job._poststring /*String*/ (_baseurl+"/getArea.php",_body);
 //BA.debugLineNum = 183;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim Nombre_Maquila As String = Starter.Nombre_Maq";
mostCurrent._nombre_maquila = mostCurrent._starter._nombre_maquila /*String*/ ;
 //BA.debugLineNum = 18;BA.debugLine="Private Scanner As GoogleCodeScanner";
mostCurrent._scanner = new b4a.example.googlecodescanner();
 //BA.debugLineNum = 19;BA.debugLine="Private LB_Bienvenido_NombreUsuario As Label";
mostCurrent._lb_bienvenido_nombreusuario = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private ET_DatosArea As EditText";
mostCurrent._et_datosarea = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 21;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(b4a.example.httpjob _job) throws Exception{
String _res = "";
int _startindex = 0;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _data = null;
String _msgfail = "";
String _nombre = "";
String _descripcion = "";
String _numerocar = "";
String _texto = "";
 //BA.debugLineNum = 73;BA.debugLine="Sub JobDone(Job As HttpJob)";
 //BA.debugLineNum = 74;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 75;BA.debugLine="If Job.Success Then";
if (_job._success /*boolean*/ ) { 
 //BA.debugLineNum = 76;BA.debugLine="If Job.JobName = \"JobArea\" Then";
if ((_job._jobname /*String*/ ).equals("JobArea")) { 
 //BA.debugLineNum = 77;BA.debugLine="Dim res As String";
_res = "";
 //BA.debugLineNum = 78;BA.debugLine="res = Job.GetString";
_res = _job._getstring /*String*/ ();
 //BA.debugLineNum = 79;BA.debugLine="Log(\"Respuesta API getArea: \" & res)";
anywheresoftware.b4a.keywords.Common.LogImpl("73342342","Respuesta API getArea: "+_res,0);
 //BA.debugLineNum = 82;BA.debugLine="Dim startIndex As Int";
_startindex = 0;
 //BA.debugLineNum = 83;BA.debugLine="startIndex = res.IndexOf(\"{\")";
_startindex = _res.indexOf("{");
 //BA.debugLineNum = 84;BA.debugLine="If startIndex > -1 Then";
if (_startindex>-1) { 
 //BA.debugLineNum = 85;BA.debugLine="res = res.SubString(startIndex)";
_res = _res.substring(_startindex);
 };
 //BA.debugLineNum = 89;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 90;BA.debugLine="parser.Initialize(res)";
_parser.Initialize(_res);
 //BA.debugLineNum = 91;BA.debugLine="Try";
try { //BA.debugLineNum = 92;BA.debugLine="Dim root As Map";
_root = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 93;BA.debugLine="root = parser.NextObject";
_root = _parser.NextObject();
 //BA.debugLineNum = 96;BA.debugLine="Dim data As Map";
_data = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 97;BA.debugLine="data.Initialize";
_data.Initialize();
 //BA.debugLineNum = 98;BA.debugLine="If root.ContainsKey(\"success\") Then";
if (_root.ContainsKey((Object)("success"))) { 
 //BA.debugLineNum = 99;BA.debugLine="If root.Get(\"success\") = True Then";
if ((_root.Get((Object)("success"))).equals((Object)(anywheresoftware.b4a.keywords.Common.True))) { 
 //BA.debugLineNum = 100;BA.debugLine="If root.ContainsKey(\"data\") Then";
if (_root.ContainsKey((Object)("data"))) { 
 //BA.debugLineNum = 101;BA.debugLine="data = root.Get(\"data\")";
_data = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_root.Get((Object)("data"))));
 }else {
 //BA.debugLineNum = 103;BA.debugLine="data.Initialize";
_data.Initialize();
 };
 }else {
 //BA.debugLineNum = 106;BA.debugLine="Dim msgFail As String";
_msgfail = "";
 //BA.debugLineNum = 107;BA.debugLine="msgFail = \"Área no encontrada\"";
_msgfail = "Área no encontrada";
 //BA.debugLineNum = 108;BA.debugLine="If root.ContainsKey(\"message\") Then msgFail";
if (_root.ContainsKey((Object)("message"))) { 
_msgfail = BA.ObjectToString(_root.Get((Object)("message")));};
 //BA.debugLineNum = 109;BA.debugLine="ToastMessageShow(msgFail, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(_msgfail),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 110;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 111;BA.debugLine="Return";
if (true) return "";
 };
 }else {
 //BA.debugLineNum = 114;BA.debugLine="data = root";
_data = _root;
 };
 //BA.debugLineNum = 118;BA.debugLine="Dim nombre As String";
_nombre = "";
 //BA.debugLineNum = 119;BA.debugLine="nombre = \"\"";
_nombre = "";
 //BA.debugLineNum = 120;BA.debugLine="If data.ContainsKey(\"Nombre_Area\") Then";
if (_data.ContainsKey((Object)("Nombre_Area"))) { 
 //BA.debugLineNum = 121;BA.debugLine="nombre = data.Get(\"Nombre_Area\")";
_nombre = BA.ObjectToString(_data.Get((Object)("Nombre_Area")));
 }else if(_data.ContainsKey((Object)("Nombre"))) { 
 //BA.debugLineNum = 123;BA.debugLine="nombre = data.Get(\"Nombre\")";
_nombre = BA.ObjectToString(_data.Get((Object)("Nombre")));
 }else if(_data.ContainsKey((Object)("NombreArea"))) { 
 //BA.debugLineNum = 125;BA.debugLine="nombre = data.Get(\"NombreArea\")";
_nombre = BA.ObjectToString(_data.Get((Object)("NombreArea")));
 };
 //BA.debugLineNum = 129;BA.debugLine="If data.ContainsKey(\"Id_Area\") Then";
if (_data.ContainsKey((Object)("Id_Area"))) { 
 //BA.debugLineNum = 130;BA.debugLine="Starter.Id_Area = data.Get(\"Id_Area\")";
mostCurrent._starter._id_area /*int*/  = (int)(BA.ObjectToNumber(_data.Get((Object)("Id_Area"))));
 };
 //BA.debugLineNum = 133;BA.debugLine="Dim descripcion As String";
_descripcion = "";
 //BA.debugLineNum = 134;BA.debugLine="descripcion = \"\"";
_descripcion = "";
 //BA.debugLineNum = 135;BA.debugLine="If data.ContainsKey(\"Descripcion_Area\") Then";
if (_data.ContainsKey((Object)("Descripcion_Area"))) { 
 //BA.debugLineNum = 136;BA.debugLine="descripcion = data.Get(\"Descripcion_Area\")";
_descripcion = BA.ObjectToString(_data.Get((Object)("Descripcion_Area")));
 }else if(_data.ContainsKey((Object)("Descripcion"))) { 
 //BA.debugLineNum = 138;BA.debugLine="descripcion = data.Get(\"Descripcion\")";
_descripcion = BA.ObjectToString(_data.Get((Object)("Descripcion")));
 }else if(_data.ContainsKey((Object)("DescripcionArea"))) { 
 //BA.debugLineNum = 140;BA.debugLine="descripcion = data.Get(\"DescripcionArea\")";
_descripcion = BA.ObjectToString(_data.Get((Object)("DescripcionArea")));
 };
 //BA.debugLineNum = 143;BA.debugLine="Dim numeroCAR As String";
_numerocar = "";
 //BA.debugLineNum = 144;BA.debugLine="numeroCAR = \"\"";
_numerocar = "";
 //BA.debugLineNum = 145;BA.debugLine="If data.ContainsKey(\"NumeroCAR_Area\") Then";
if (_data.ContainsKey((Object)("NumeroCAR_Area"))) { 
 //BA.debugLineNum = 146;BA.debugLine="numeroCAR = data.Get(\"NumeroCAR_Area\")";
_numerocar = BA.ObjectToString(_data.Get((Object)("NumeroCAR_Area")));
 }else if(_data.ContainsKey((Object)("NumeroCAR"))) { 
 //BA.debugLineNum = 148;BA.debugLine="numeroCAR = data.Get(\"NumeroCAR\")";
_numerocar = BA.ObjectToString(_data.Get((Object)("NumeroCAR")));
 }else if(_data.ContainsKey((Object)("Numero_CAR"))) { 
 //BA.debugLineNum = 150;BA.debugLine="numeroCAR = data.Get(\"Numero_CAR\")";
_numerocar = BA.ObjectToString(_data.Get((Object)("Numero_CAR")));
 };
 //BA.debugLineNum = 158;BA.debugLine="Dim texto As String";
_texto = "";
 //BA.debugLineNum = 159;BA.debugLine="texto = \"Nombre del Area: \" & nombre & CRLF";
_texto = "Nombre del Area: "+_nombre+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 160;BA.debugLine="texto = texto & \"Descripción: \" & descripcion";
_texto = _texto+"Descripción: "+_descripcion+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 161;BA.debugLine="texto = texto & \"Numero C.A.R: \" & numeroCAR";
_texto = _texto+"Numero C.A.R: "+_numerocar;
 //BA.debugLineNum = 162;BA.debugLine="ET_DatosArea.Text = texto";
mostCurrent._et_datosarea.setText(BA.ObjectToCharSequence(_texto));
 } 
       catch (Exception e73) {
			processBA.setLastException(e73); //BA.debugLineNum = 165;BA.debugLine="ToastMessageShow(\"Respuesta inválida del servi";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Respuesta inválida del servidor"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 166;BA.debugLine="Log(\"Error parse JSON getArea: \" & LastExcepti";
anywheresoftware.b4a.keywords.Common.LogImpl("73342429","Error parse JSON getArea: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 }else {
 //BA.debugLineNum = 170;BA.debugLine="ToastMessageShow(\"Error de red: \" & Job.ErrorMes";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error de red: "+_job._errormessage /*String*/ ),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 172;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 173;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim BaseUrl As String = \"https://humane-pelican-b";
_baseurl = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api";
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _urlencode(String _s) throws Exception{
anywheresoftware.b4j.object.JavaObject _jo = null;
String _result = "";
 //BA.debugLineNum = 37;BA.debugLine="Sub URLEncode(s As String) As String";
 //BA.debugLineNum = 38;BA.debugLine="Dim jo As JavaObject";
_jo = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 39;BA.debugLine="jo.InitializeStatic(\"java.net.URLEncoder\")";
_jo.InitializeStatic("java.net.URLEncoder");
 //BA.debugLineNum = 40;BA.debugLine="Dim result As String";
_result = "";
 //BA.debugLineNum = 41;BA.debugLine="result = jo.RunMethod(\"encode\", Array(s, \"UTF-8\")";
_result = BA.ObjectToString(_jo.RunMethod("encode",new Object[]{(Object)(_s),(Object)("UTF-8")}));
 //BA.debugLineNum = 42;BA.debugLine="Return result";
if (true) return _result;
 //BA.debugLineNum = 43;BA.debugLine="End Sub";
return "";
}
}
