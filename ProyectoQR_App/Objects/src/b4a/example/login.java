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

public class login extends Activity implements B4AActivity{
	public static login mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.login");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (login).");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.login");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.login", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (login) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (login) Resume **");
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
		return login.class;
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
            BA.LogInfo("** Activity (login) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (login) Pause event (activity is not paused). **");
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
            login mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (login) Resume **");
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
public anywheresoftware.b4a.objects.EditTextWrapper _nombre_usuario = null;
public anywheresoftware.b4a.objects.EditTextWrapper _password_usuario = null;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.menuprincipal _menuprincipal = null;
public b4a.example.httputils2service _httputils2service = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 25;BA.debugLine="Activity.LoadLayout(\"loginscreen\")";
mostCurrent._activity.LoadLayout("loginscreen",mostCurrent.activityBA);
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
public static String  _button_submit_click() throws Exception{
String _usuario = "";
String _pass = "";
b4a.example.httpjob _job = null;
String _body = "";
 //BA.debugLineNum = 46;BA.debugLine="Private Sub Button_Submit_Click";
 //BA.debugLineNum = 47;BA.debugLine="Dim usuario As String = Nombre_Usuario.Text.Trim";
_usuario = mostCurrent._nombre_usuario.getText().trim();
 //BA.debugLineNum = 48;BA.debugLine="Dim pass As String = Password_Usuario.Text";
_pass = mostCurrent._password_usuario.getText();
 //BA.debugLineNum = 50;BA.debugLine="If usuario = \"\" Or pass = \"\" Then";
if ((_usuario).equals("") || (_pass).equals("")) { 
 //BA.debugLineNum = 51;BA.debugLine="ToastMessageShow(\"Completa ambos campos\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Completa ambos campos"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 52;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 55;BA.debugLine="ProgressDialogShow(\"Iniciando sesión...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Iniciando sesión..."));
 //BA.debugLineNum = 57;BA.debugLine="Dim job As HttpJob";
_job = new b4a.example.httpjob();
 //BA.debugLineNum = 58;BA.debugLine="job.Initialize(\"LoginJob\", Me)";
_job._initialize /*String*/ (processBA,"LoginJob",login.getObject());
 //BA.debugLineNum = 59;BA.debugLine="Dim body As String";
_body = "";
 //BA.debugLineNum = 60;BA.debugLine="body = \"nombre_usuario=\" & URLEncode(usuario) & \"";
_body = "nombre_usuario="+_urlencode(_usuario)+"&password="+_urlencode(_pass);
 //BA.debugLineNum = 61;BA.debugLine="job.PostString(BaseUrl & \"/login.php\", body)";
_job._poststring /*String*/ (_baseurl+"/login.php",_body);
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 13;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 17;BA.debugLine="Private Nombre_Usuario As EditText";
mostCurrent._nombre_usuario = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private Password_Usuario As EditText";
mostCurrent._password_usuario = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(b4a.example.httpjob _job) throws Exception{
String _res = "";
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _data = null;
String _token = "";
int _idusuario = 0;
String _nombre = "";
String _puesto = "";
anywheresoftware.b4a.objects.collections.Map _sessionmap = null;
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _jg = null;
String _msg = "";
 //BA.debugLineNum = 64;BA.debugLine="Sub JobDone(Job As HttpJob)";
 //BA.debugLineNum = 65;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 66;BA.debugLine="If Job.Success Then";
if (_job._success /*boolean*/ ) { 
 //BA.debugLineNum = 67;BA.debugLine="Dim res As String = Job.GetString";
_res = _job._getstring /*String*/ ();
 //BA.debugLineNum = 68;BA.debugLine="Log(\"Respuesta login: \" & res)";
anywheresoftware.b4a.keywords.Common.LogImpl("3786436","Respuesta login: "+_res,0);
 //BA.debugLineNum = 69;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 70;BA.debugLine="parser.Initialize(res)";
_parser.Initialize(_res);
 //BA.debugLineNum = 71;BA.debugLine="Try";
try { //BA.debugLineNum = 72;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 73;BA.debugLine="If root.ContainsKey(\"success\") And root.Get(\"su";
if (_root.ContainsKey((Object)("success")) && (_root.Get((Object)("success"))).equals((Object)(anywheresoftware.b4a.keywords.Common.True))) { 
 //BA.debugLineNum = 74;BA.debugLine="Dim data As Map = root.Get(\"data\")";
_data = new anywheresoftware.b4a.objects.collections.Map();
_data = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_root.Get((Object)("data"))));
 //BA.debugLineNum = 76;BA.debugLine="Dim token As String = data.Get(\"token\")";
_token = BA.ObjectToString(_data.Get((Object)("token")));
 //BA.debugLineNum = 77;BA.debugLine="Dim idUsuario As Int = data.Get(\"Id_Usuario\")";
_idusuario = (int)(BA.ObjectToNumber(_data.Get((Object)("Id_Usuario"))));
 //BA.debugLineNum = 78;BA.debugLine="Dim nombre As String = data.Get(\"Nombre_Usuari";
_nombre = BA.ObjectToString(_data.Get((Object)("Nombre_Usuario")));
 //BA.debugLineNum = 79;BA.debugLine="Dim puesto As String = data.Get(\"Puesto_Usuari";
_puesto = BA.ObjectToString(_data.Get((Object)("Puesto_Usuario")));
 //BA.debugLineNum = 82;BA.debugLine="Dim sessionMap As Map";
_sessionmap = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 83;BA.debugLine="sessionMap.Initialize";
_sessionmap.Initialize();
 //BA.debugLineNum = 84;BA.debugLine="sessionMap.Put(\"Id_Usuario\", idUsuario)";
_sessionmap.Put((Object)("Id_Usuario"),(Object)(_idusuario));
 //BA.debugLineNum = 85;BA.debugLine="sessionMap.Put(\"Nombre_Usuario\", nombre)";
_sessionmap.Put((Object)("Nombre_Usuario"),(Object)(_nombre));
 //BA.debugLineNum = 86;BA.debugLine="sessionMap.Put(\"Telefono_Usuario\", data.Get(\"T";
_sessionmap.Put((Object)("Telefono_Usuario"),_data.Get((Object)("Telefono_Usuario")));
 //BA.debugLineNum = 87;BA.debugLine="sessionMap.Put(\"Puesto_Usuario\", data.Get(\"Pue";
_sessionmap.Put((Object)("Puesto_Usuario"),_data.Get((Object)("Puesto_Usuario")));
 //BA.debugLineNum = 88;BA.debugLine="sessionMap.Put(\"token\", token)";
_sessionmap.Put((Object)("token"),(Object)(_token));
 //BA.debugLineNum = 90;BA.debugLine="Dim jg As JSONGenerator";
_jg = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 91;BA.debugLine="jg.Initialize(sessionMap)";
_jg.Initialize(_sessionmap);
 //BA.debugLineNum = 93;BA.debugLine="ToastMessageShow(\"Bienvenido, \" & nombre, Fals";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Bienvenido, "+_nombre),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 96;BA.debugLine="Starter.Id_Usuario = idUsuario";
mostCurrent._starter._id_usuario /*int*/  = _idusuario;
 //BA.debugLineNum = 97;BA.debugLine="Starter.Nombre_Usuario = nombre";
mostCurrent._starter._nombre_usuario /*String*/  = _nombre;
 //BA.debugLineNum = 98;BA.debugLine="Starter.Puesto_Usuario = puesto";
mostCurrent._starter._puesto_usuario /*String*/  = _puesto;
 //BA.debugLineNum = 100;BA.debugLine="StartActivity(menuprincipal)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._menuprincipal.getObject()));
 //BA.debugLineNum = 101;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 }else {
 //BA.debugLineNum = 104;BA.debugLine="Dim msg As String = root.Get(\"message\")";
_msg = BA.ObjectToString(_root.Get((Object)("message")));
 //BA.debugLineNum = 105;BA.debugLine="ToastMessageShow(msg, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(_msg),anywheresoftware.b4a.keywords.Common.True);
 };
 } 
       catch (Exception e35) {
			processBA.setLastException(e35); //BA.debugLineNum = 108;BA.debugLine="ToastMessageShow(\"Respuesta inválida del servid";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Respuesta inválida del servidor"),anywheresoftware.b4a.keywords.Common.True);
 };
 }else {
 //BA.debugLineNum = 111;BA.debugLine="ToastMessageShow(\"Error de red: \" & Job.ErrorMes";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error de red: "+_job._errormessage /*String*/ ),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 113;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim BaseUrl As String = \"https://humane-pelican-b";
_baseurl = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api";
 //BA.debugLineNum = 11;BA.debugLine="End Sub";
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
