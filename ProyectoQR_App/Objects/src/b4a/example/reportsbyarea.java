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

public class reportsbyarea extends Activity implements B4AActivity{
	public static reportsbyarea mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.reportsbyarea");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (reportsbyarea).");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.reportsbyarea");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.reportsbyarea", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (reportsbyarea) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (reportsbyarea) Resume **");
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
		return reportsbyarea.class;
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
            BA.LogInfo("** Activity (reportsbyarea) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (reportsbyarea) Pause event (activity is not paused). **");
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
            reportsbyarea mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (reportsbyarea) Resume **");
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
public static String _areatoshow = "";
public static String _baseurltouse = "";
public anywheresoftware.b4a.objects.ListViewWrapper _lv = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnrefresh = null;
public anywheresoftware.b4a.objects.collections.Map _reportsmap = null;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.menuprincipal _menuprincipal = null;
public b4a.example.reportdialog _reportdialog = null;
public b4a.example.login _login = null;
public b4a.example.menuprincipal_maquilas _menuprincipal_maquilas = null;
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
int _screenw = 0;
 //BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 28;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 29;BA.debugLine="Dim screenW As Int = 100%x";
_screenw = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 31;BA.debugLine="lblTitle.Initialize(\"lblTitle\")";
mostCurrent._lbltitle.Initialize(mostCurrent.activityBA,"lblTitle");
 //BA.debugLineNum = 32;BA.debugLine="lblTitle.Text = \"Reportes\"";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence("Reportes"));
 //BA.debugLineNum = 33;BA.debugLine="lblTitle.TextSize = 16";
mostCurrent._lbltitle.setTextSize((float) (16));
 //BA.debugLineNum = 34;BA.debugLine="lblTitle.Gravity = Gravity.CENTER_HORIZONTAL + Gr";
mostCurrent._lbltitle.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL+anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL));
 //BA.debugLineNum = 35;BA.debugLine="Activity.AddView(lblTitle, 0, 0, screenW, 10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lbltitle.getObject()),(int) (0),(int) (0),_screenw,anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 37;BA.debugLine="btnRefresh.Initialize(\"btnRefresh\")";
mostCurrent._btnrefresh.Initialize(mostCurrent.activityBA,"btnRefresh");
 //BA.debugLineNum = 38;BA.debugLine="btnRefresh.Text = \"Refrescar\"";
mostCurrent._btnrefresh.setText(BA.ObjectToCharSequence("Refrescar"));
 //BA.debugLineNum = 39;BA.debugLine="Activity.AddView(btnRefresh, 2%x, 10%y + 4dip, 30";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._btnrefresh.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA)+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (8),mostCurrent.activityBA));
 //BA.debugLineNum = 41;BA.debugLine="lv.Initialize(\"lvReports\")";
mostCurrent._lv.Initialize(mostCurrent.activityBA,"lvReports");
 //BA.debugLineNum = 42;BA.debugLine="Activity.AddView(lv, 0, 18%y, screenW, 72%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lv.getObject()),(int) (0),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (18),mostCurrent.activityBA),_screenw,anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (72),mostCurrent.activityBA));
 //BA.debugLineNum = 44;BA.debugLine="reportsMap.Initialize";
mostCurrent._reportsmap.Initialize();
 //BA.debugLineNum = 46;BA.debugLine="If AreaToShow <> \"\" Then";
if ((_areatoshow).equals("") == false) { 
 //BA.debugLineNum = 47;BA.debugLine="lblTitle.Text = \"Reportes del área: \" & AreaToSh";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence("Reportes del área: "+_areatoshow));
 //BA.debugLineNum = 48;BA.debugLine="If BaseUrlToUse = \"\" Then";
if ((_baseurltouse).equals("")) { 
 //BA.debugLineNum = 49;BA.debugLine="BaseUrlToUse = \"https://yourserver.example/api/";
_baseurltouse = "https://yourserver.example/api/get_reports_by_area.php?area={area}";
 };
 //BA.debugLineNum = 51;BA.debugLine="FetchReports(BaseUrlToUse, AreaToShow)";
_fetchreports(_baseurltouse,_areatoshow);
 };
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 59;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 55;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 57;BA.debugLine="End Sub";
return "";
}
public static String  _btnrefresh_click() throws Exception{
 //BA.debugLineNum = 63;BA.debugLine="Sub btnRefresh_Click";
 //BA.debugLineNum = 64;BA.debugLine="If AreaToShow = \"\" Or BaseUrlToUse = \"\" Then";
if ((_areatoshow).equals("") || (_baseurltouse).equals("")) { 
 //BA.debugLineNum = 65;BA.debugLine="ToastMessageShow(\"Asigne AreaToShow y BaseUrlToU";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Asigne AreaToShow y BaseUrlToUse antes de iniciar."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 66;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 68;BA.debugLine="FetchReports(BaseUrlToUse, AreaToShow)";
_fetchreports(_baseurltouse,_areatoshow);
 //BA.debugLineNum = 69;BA.debugLine="End Sub";
return "";
}
public static String  _fetchreports(String _baseurl,String _areacode) throws Exception{
String _url = "";
b4a.example.httpjob _j = null;
 //BA.debugLineNum = 72;BA.debugLine="Sub FetchReports(BaseUrl As String, AreaCode As St";
 //BA.debugLineNum = 73;BA.debugLine="Dim url As String = BaseUrl";
_url = _baseurl;
 //BA.debugLineNum = 74;BA.debugLine="If url.Contains(\"{area}\") Then";
if (_url.contains("{area}")) { 
 //BA.debugLineNum = 75;BA.debugLine="url = url.Replace(\"{area}\", AreaCode)";
_url = _url.replace("{area}",_areacode);
 }else {
 //BA.debugLineNum = 77;BA.debugLine="If url.Contains(\"?\") Then";
if (_url.contains("?")) { 
 //BA.debugLineNum = 78;BA.debugLine="url = url & \"&area=\" & AreaCode";
_url = _url+"&area="+_areacode;
 }else {
 //BA.debugLineNum = 80;BA.debugLine="url = url & \"?area=\" & AreaCode";
_url = _url+"?area="+_areacode;
 };
 };
 //BA.debugLineNum = 84;BA.debugLine="ProgressDialogShow(\"Cargando reportes...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Cargando reportes..."));
 //BA.debugLineNum = 85;BA.debugLine="Dim j As HttpJob";
_j = new b4a.example.httpjob();
 //BA.debugLineNum = 86;BA.debugLine="j.Initialize(\"getReports\", Me)";
_j._initialize /*String*/ (processBA,"getReports",reportsbyarea.getObject());
 //BA.debugLineNum = 87;BA.debugLine="j.Download(url)";
_j._download /*String*/ (_url);
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 21;BA.debugLine="Private lv As ListView";
mostCurrent._lv = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private lblTitle As Label";
mostCurrent._lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private btnRefresh As Button";
mostCurrent._btnrefresh = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private reportsMap As Map ' index -> Map (report";
mostCurrent._reportsmap = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(b4a.example.httpjob _job) throws Exception{
String _res = "";
int _firstnonspace = 0;
int _i = 0;
String _c = "";
String _c2 = "";
int _idxobj = 0;
int _idxarr = 0;
int _startidx = 0;
String _restoparse = "";
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
Object _root = null;
String _low = "";
String _snippet = "";
anywheresoftware.b4a.objects.collections.List _list = null;
anywheresoftware.b4a.objects.collections.Map _mp = null;
anywheresoftware.b4a.objects.collections.List _keys = null;
String _k = "";
Object _v = null;
Object _rawitem = null;
anywheresoftware.b4a.objects.collections.Map _reportcontent = null;
anywheresoftware.b4a.objects.collections.Map _candidate = null;
anywheresoftware.b4a.objects.collections.List _possiblefields = null;
String _f = "";
String _s = "";
anywheresoftware.b4a.objects.collections.JSONParser _p2 = null;
Object _maybe = null;
anywheresoftware.b4a.objects.collections.List _l2 = null;
String _s2 = "";
anywheresoftware.b4a.objects.collections.JSONParser _p3 = null;
Object _maybe2 = null;
String _title = "";
anywheresoftware.b4a.objects.collections.Map _areamap = null;
 //BA.debugLineNum = 91;BA.debugLine="Sub JobDone(Job As HttpJob)";
 //BA.debugLineNum = 92;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 93;BA.debugLine="If Job.Success = False Then";
if (_job._success /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 94;BA.debugLine="ToastMessageShow(\"Error al obtener reportes: \" &";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error al obtener reportes: "+_job._errormessage /*String*/ ),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 95;BA.debugLine="Log(\"JobDone: HTTP error: \" & Job.ErrorMessage)";
anywheresoftware.b4a.keywords.Common.LogImpl("73997700","JobDone: HTTP error: "+_job._errormessage /*String*/ ,0);
 //BA.debugLineNum = 96;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 97;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 100;BA.debugLine="Dim res As String = Job.GetString";
_res = _job._getstring /*String*/ ();
 //BA.debugLineNum = 101;BA.debugLine="If res = Null Then res = \"\"";
if (_res== null) { 
_res = "";};
 //BA.debugLineNum = 103;BA.debugLine="Try";
try { //BA.debugLineNum = 104;BA.debugLine="res = res.Replace(Chr(65279), \"\")";
_res = _res.replace(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (65279))),"");
 } 
       catch (Exception e13) {
			processBA.setLastException(e13); //BA.debugLineNum = 106;BA.debugLine="Log(\"JobDone: BOM replace failed: \" & LastExcept";
anywheresoftware.b4a.keywords.Common.LogImpl("73997711","JobDone: BOM replace failed: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 108;BA.debugLine="res = res.Trim";
_res = _res.trim();
 //BA.debugLineNum = 110;BA.debugLine="If res = \"\" Then";
if ((_res).equals("")) { 
 //BA.debugLineNum = 111;BA.debugLine="ToastMessageShow(\"Respuesta vacía del servidor.\"";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Respuesta vacía del servidor."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 112;BA.debugLine="Log(\"JobDone: empty response\")";
anywheresoftware.b4a.keywords.Common.LogImpl("73997717","JobDone: empty response",0);
 //BA.debugLineNum = 113;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 114;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 118;BA.debugLine="Dim firstNonSpace As Int = -1";
_firstnonspace = (int) (-1);
 //BA.debugLineNum = 119;BA.debugLine="For i = 0 To res.Length - 1";
{
final int step23 = 1;
final int limit23 = (int) (_res.length()-1);
_i = (int) (0) ;
for (;_i <= limit23 ;_i = _i + step23 ) {
 //BA.debugLineNum = 120;BA.debugLine="Dim c As String = res.SubString2(i, i + 1)";
_c = _res.substring(_i,(int) (_i+1));
 //BA.debugLineNum = 121;BA.debugLine="If c <> \" \" And c <> Chr(10) And c <> Chr(13) An";
if ((_c).equals(" ") == false && (_c).equals(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))) == false && (_c).equals(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (13)))) == false && (_c).equals(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (9)))) == false) { 
 //BA.debugLineNum = 122;BA.debugLine="firstNonSpace = i";
_firstnonspace = _i;
 //BA.debugLineNum = 123;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 126;BA.debugLine="If firstNonSpace >= 0 Then";
if (_firstnonspace>=0) { 
 //BA.debugLineNum = 127;BA.debugLine="Dim c2 As String = res.SubString2(firstNonSpace,";
_c2 = _res.substring(_firstnonspace,(int) (_firstnonspace+1));
 //BA.debugLineNum = 128;BA.debugLine="If c2 = \"<\" Then";
if ((_c2).equals("<")) { 
 //BA.debugLineNum = 129;BA.debugLine="ToastMessageShow(\"Respuesta HTML recibida en lu";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Respuesta HTML recibida en lugar de JSON (ver logs)."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 130;BA.debugLine="Log(\"JobDone: HTML response snippet: \" & res.Su";
anywheresoftware.b4a.keywords.Common.LogImpl("73997735","JobDone: HTML response snippet: "+_res.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(_res.length(),500))),0);
 //BA.debugLineNum = 131;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 132;BA.debugLine="Return";
if (true) return "";
 };
 };
 //BA.debugLineNum = 137;BA.debugLine="Dim idxObj As Int = res.IndexOf(\"{\")";
_idxobj = _res.indexOf("{");
 //BA.debugLineNum = 138;BA.debugLine="Dim idxArr As Int = res.IndexOf(\"[\")";
_idxarr = _res.indexOf("[");
 //BA.debugLineNum = 139;BA.debugLine="Dim startIdx As Int = -1";
_startidx = (int) (-1);
 //BA.debugLineNum = 140;BA.debugLine="If idxObj = -1 Then";
if (_idxobj==-1) { 
 //BA.debugLineNum = 141;BA.debugLine="startIdx = idxArr";
_startidx = _idxarr;
 }else {
 //BA.debugLineNum = 143;BA.debugLine="If idxArr = -1 Then";
if (_idxarr==-1) { 
 //BA.debugLineNum = 144;BA.debugLine="startIdx = idxObj";
_startidx = _idxobj;
 }else {
 //BA.debugLineNum = 146;BA.debugLine="startIdx = Min(idxObj, idxArr)";
_startidx = (int) (anywheresoftware.b4a.keywords.Common.Min(_idxobj,_idxarr));
 };
 };
 //BA.debugLineNum = 150;BA.debugLine="Dim resToParse As String";
_restoparse = "";
 //BA.debugLineNum = 151;BA.debugLine="If startIdx > 0 Then";
if (_startidx>0) { 
 //BA.debugLineNum = 152;BA.debugLine="resToParse = res.SubString(startIdx)";
_restoparse = _res.substring(_startidx);
 //BA.debugLineNum = 153;BA.debugLine="Log(\"JobDone: trimming response before first JSO";
anywheresoftware.b4a.keywords.Common.LogImpl("73997758","JobDone: trimming response before first JSON char. trimmed snippet: "+_restoparse.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(_restoparse.length(),200))),0);
 }else {
 //BA.debugLineNum = 155;BA.debugLine="resToParse = res";
_restoparse = _res;
 };
 //BA.debugLineNum = 158;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 159;BA.debugLine="parser.Initialize(resToParse)";
_parser.Initialize(_restoparse);
 //BA.debugLineNum = 160;BA.debugLine="Dim root As Object";
_root = new Object();
 //BA.debugLineNum = 161;BA.debugLine="Try";
try { //BA.debugLineNum = 162;BA.debugLine="root = parser.NextValue";
_root = _parser.NextValue();
 } 
       catch (Exception e64) {
			processBA.setLastException(e64); //BA.debugLineNum = 164;BA.debugLine="Log(\"JobDone parse error first attempt: \" & Last";
anywheresoftware.b4a.keywords.Common.LogImpl("73997769","JobDone parse error first attempt: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 166;BA.debugLine="Try";
try { //BA.debugLineNum = 167;BA.debugLine="parser.Initialize(res)";
_parser.Initialize(_res);
 //BA.debugLineNum = 168;BA.debugLine="root = parser.NextValue";
_root = _parser.NextValue();
 } 
       catch (Exception e69) {
			processBA.setLastException(e69); //BA.debugLineNum = 170;BA.debugLine="Log(\"JobDone parse error second attempt: \" & La";
anywheresoftware.b4a.keywords.Common.LogImpl("73997775","JobDone parse error second attempt: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 171;BA.debugLine="Dim low As String = res.ToLowerCase";
_low = _res.toLowerCase();
 //BA.debugLineNum = 172;BA.debugLine="If low = \"null\" Or low = \"true\" Or low = \"false";
if ((_low).equals("null") || (_low).equals("true") || (_low).equals("false") || anywheresoftware.b4a.keywords.Common.IsNumber(_res)) { 
 //BA.debugLineNum = 173;BA.debugLine="ToastMessageShow(\"Respuesta válida pero no es";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Respuesta válida pero no es un objeto/array JSON esperado."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 174;BA.debugLine="Log(\"JobDone: primitive response: \" & res)";
anywheresoftware.b4a.keywords.Common.LogImpl("73997779","JobDone: primitive response: "+_res,0);
 //BA.debugLineNum = 175;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 176;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 178;BA.debugLine="Dim snippet As String = res.SubString2(0, Min(r";
_snippet = _res.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(_res.length(),800)));
 //BA.debugLineNum = 179;BA.debugLine="ToastMessageShow(\"No se pudo parsear JSON (mira";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se pudo parsear JSON (mira logs)."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 180;BA.debugLine="Log(\"JobDone final parse fail. Response snippet";
anywheresoftware.b4a.keywords.Common.LogImpl("73997785","JobDone final parse fail. Response snippet: "+_snippet,0);
 //BA.debugLineNum = 181;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 182;BA.debugLine="Return";
if (true) return "";
 };
 };
 //BA.debugLineNum = 187;BA.debugLine="Dim list As List";
_list = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 188;BA.debugLine="list.Initialize";
_list.Initialize();
 //BA.debugLineNum = 190;BA.debugLine="Try";
try { //BA.debugLineNum = 191;BA.debugLine="If root Is List Then";
if (_root instanceof java.util.List) { 
 //BA.debugLineNum = 192;BA.debugLine="list = root";
_list = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root));
 }else if(_root instanceof java.util.Map) { 
 //BA.debugLineNum = 194;BA.debugLine="Dim mp As Map = root";
_mp = new anywheresoftware.b4a.objects.collections.Map();
_mp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_root));
 //BA.debugLineNum = 195;BA.debugLine="If mp.ContainsKey(\"data\") And mp.Get(\"data\") Is";
if (_mp.ContainsKey((Object)("data")) && _mp.Get((Object)("data")) instanceof java.util.List) { 
 //BA.debugLineNum = 196;BA.debugLine="list = mp.Get(\"data\")";
_list = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_mp.Get((Object)("data"))));
 }else if(_mp.ContainsKey((Object)("reportes")) && _mp.Get((Object)("reportes")) instanceof java.util.List) { 
 //BA.debugLineNum = 198;BA.debugLine="list = mp.Get(\"reportes\")";
_list = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_mp.Get((Object)("reportes"))));
 }else {
 //BA.debugLineNum = 201;BA.debugLine="Dim keys As List = mp.Keys";
_keys = new anywheresoftware.b4a.objects.collections.List();
_keys = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_mp.Keys()));
 //BA.debugLineNum = 202;BA.debugLine="For Each k As String In keys";
{
final anywheresoftware.b4a.BA.IterableList group97 = _keys;
final int groupLen97 = group97.getSize()
;int index97 = 0;
;
for (; index97 < groupLen97;index97++){
_k = BA.ObjectToString(group97.Get(index97));
 //BA.debugLineNum = 203;BA.debugLine="Dim v As Object = mp.Get(k)";
_v = _mp.Get((Object)(_k));
 //BA.debugLineNum = 204;BA.debugLine="If v Is List Then";
if (_v instanceof java.util.List) { 
 //BA.debugLineNum = 205;BA.debugLine="list = v";
_list = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_v));
 //BA.debugLineNum = 206;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 210;BA.debugLine="If (list.IsInitialized = False Or list.Size =";
if ((_list.IsInitialized()==anywheresoftware.b4a.keywords.Common.False || _list.getSize()==0)) { 
 //BA.debugLineNum = 211;BA.debugLine="If mp.ContainsKey(\"area\") Or mp.ContainsKey(\"";
if (_mp.ContainsKey((Object)("area")) || _mp.ContainsKey((Object)("car_reports"))) { 
 //BA.debugLineNum = 212;BA.debugLine="list.Initialize";
_list.Initialize();
 //BA.debugLineNum = 213;BA.debugLine="list.Add(mp)";
_list.Add((Object)(_mp.getObject()));
 };
 };
 };
 };
 } 
       catch (Exception e113) {
			processBA.setLastException(e113); //BA.debugLineNum = 219;BA.debugLine="Log(\"JobDone building list error: \" & LastExcept";
anywheresoftware.b4a.keywords.Common.LogImpl("73997824","JobDone building list error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 220;BA.debugLine="ToastMessageShow(\"Error procesando estructura de";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error procesando estructura de respuesta."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 221;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 222;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 226;BA.debugLine="reportsMap.Clear";
mostCurrent._reportsmap.Clear();
 //BA.debugLineNum = 227;BA.debugLine="lv.Clear";
mostCurrent._lv.Clear();
 //BA.debugLineNum = 229;BA.debugLine="For i = 0 To list.Size - 1";
{
final int step120 = 1;
final int limit120 = (int) (_list.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit120 ;_i = _i + step120 ) {
 //BA.debugLineNum = 230;BA.debugLine="Dim rawItem As Object = list.Get(i)";
_rawitem = _list.Get(_i);
 //BA.debugLineNum = 231;BA.debugLine="Dim reportContent As Map";
_reportcontent = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 232;BA.debugLine="reportContent = Null";
_reportcontent = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 234;BA.debugLine="If rawItem Is Map Then";
if (_rawitem instanceof java.util.Map) { 
 //BA.debugLineNum = 235;BA.debugLine="Dim candidate As Map = rawItem";
_candidate = new anywheresoftware.b4a.objects.collections.Map();
_candidate = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_rawitem));
 //BA.debugLineNum = 236;BA.debugLine="If candidate.ContainsKey(\"area\") Or candidate.C";
if (_candidate.ContainsKey((Object)("area")) || _candidate.ContainsKey((Object)("car_reports"))) { 
 //BA.debugLineNum = 237;BA.debugLine="reportContent = candidate";
_reportcontent = _candidate;
 }else {
 //BA.debugLineNum = 239;BA.debugLine="Dim possibleFields As List";
_possiblefields = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 240;BA.debugLine="possibleFields = Array As String(\"JSON_Reporte";
_possiblefields = anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"JSON_Reporte","JSON","reporte_json","Contenido","Reporte","data"});
 //BA.debugLineNum = 241;BA.debugLine="For Each f As String In possibleFields";
{
final anywheresoftware.b4a.BA.IterableList group131 = _possiblefields;
final int groupLen131 = group131.getSize()
;int index131 = 0;
;
for (; index131 < groupLen131;index131++){
_f = BA.ObjectToString(group131.Get(index131));
 //BA.debugLineNum = 242;BA.debugLine="If candidate.ContainsKey(f) Then";
if (_candidate.ContainsKey((Object)(_f))) { 
 //BA.debugLineNum = 243;BA.debugLine="Try";
try { //BA.debugLineNum = 244;BA.debugLine="Dim s As String = candidate.Get(f)";
_s = BA.ObjectToString(_candidate.Get((Object)(_f)));
 //BA.debugLineNum = 245;BA.debugLine="If s <> \"\" Then";
if ((_s).equals("") == false) { 
 //BA.debugLineNum = 246;BA.debugLine="Dim p2 As JSONParser";
_p2 = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 247;BA.debugLine="p2.Initialize(s)";
_p2.Initialize(_s);
 //BA.debugLineNum = 248;BA.debugLine="Dim maybe As Object";
_maybe = new Object();
 //BA.debugLineNum = 249;BA.debugLine="maybe = p2.NextValue";
_maybe = _p2.NextValue();
 //BA.debugLineNum = 250;BA.debugLine="If maybe Is Map Then";
if (_maybe instanceof java.util.Map) { 
 //BA.debugLineNum = 251;BA.debugLine="reportContent = maybe";
_reportcontent = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_maybe));
 //BA.debugLineNum = 252;BA.debugLine="Exit";
if (true) break;
 };
 //BA.debugLineNum = 254;BA.debugLine="If maybe Is List Then";
if (_maybe instanceof java.util.List) { 
 //BA.debugLineNum = 255;BA.debugLine="Dim l2 As List = maybe";
_l2 = new anywheresoftware.b4a.objects.collections.List();
_l2 = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_maybe));
 //BA.debugLineNum = 256;BA.debugLine="If l2.Size > 0 And l2.Get(0) Is Map Then";
if (_l2.getSize()>0 && _l2.Get((int) (0)) instanceof java.util.Map) { 
 //BA.debugLineNum = 257;BA.debugLine="reportContent = l2.Get(0)";
_reportcontent = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_l2.Get((int) (0))));
 //BA.debugLineNum = 258;BA.debugLine="Exit";
if (true) break;
 };
 };
 };
 } 
       catch (Exception e153) {
			processBA.setLastException(e153); //BA.debugLineNum = 263;BA.debugLine="Log(\"Ignored parse error for field \" & f &";
anywheresoftware.b4a.keywords.Common.LogImpl("73997868","Ignored parse error for field "+_f+": "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 }
};
 };
 }else {
 //BA.debugLineNum = 269;BA.debugLine="Try";
try { //BA.debugLineNum = 270;BA.debugLine="Dim s2 As String = rawItem";
_s2 = BA.ObjectToString(_rawitem);
 //BA.debugLineNum = 271;BA.debugLine="Dim p3 As JSONParser";
_p3 = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 272;BA.debugLine="p3.Initialize(s2)";
_p3.Initialize(_s2);
 //BA.debugLineNum = 273;BA.debugLine="Dim maybe2 As Object = p3.NextValue";
_maybe2 = _p3.NextValue();
 //BA.debugLineNum = 274;BA.debugLine="If maybe2 Is Map Then";
if (_maybe2 instanceof java.util.Map) { 
 //BA.debugLineNum = 275;BA.debugLine="reportContent = maybe2";
_reportcontent = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_maybe2));
 };
 } 
       catch (Exception e168) {
			processBA.setLastException(e168); //BA.debugLineNum = 278;BA.debugLine="Log(\"Ignored non-map rawItem parse error: \" &";
anywheresoftware.b4a.keywords.Common.LogImpl("73997883","Ignored non-map rawItem parse error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 282;BA.debugLine="If reportContent = Null Then";
if (_reportcontent== null) { 
 //BA.debugLineNum = 283;BA.debugLine="reportContent.Initialize";
_reportcontent.Initialize();
 //BA.debugLineNum = 284;BA.debugLine="reportContent.Put(\"raw\", rawItem)";
_reportcontent.Put((Object)("raw"),_rawitem);
 };
 //BA.debugLineNum = 288;BA.debugLine="Dim title As String = \"\"";
_title = "";
 //BA.debugLineNum = 289;BA.debugLine="If reportContent.ContainsKey(\"area\") Then";
if (_reportcontent.ContainsKey((Object)("area"))) { 
 //BA.debugLineNum = 290;BA.debugLine="Try";
try { //BA.debugLineNum = 291;BA.debugLine="Dim areaMap As Map = reportContent.Get(\"area\")";
_areamap = new anywheresoftware.b4a.objects.collections.Map();
_areamap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_reportcontent.Get((Object)("area"))));
 //BA.debugLineNum = 292;BA.debugLine="If areaMap.ContainsKey(\"area_name\") Then title";
if (_areamap.ContainsKey((Object)("area_name"))) { 
_title = BA.ObjectToString(_areamap.Get((Object)("area_name")));};
 //BA.debugLineNum = 293;BA.debugLine="If title = \"\" And areaMap.ContainsKey(\"areaNam";
if ((_title).equals("") && _areamap.ContainsKey((Object)("areaName"))) { 
_title = BA.ObjectToString(_areamap.Get((Object)("areaName")));};
 } 
       catch (Exception e182) {
			processBA.setLastException(e182); //BA.debugLineNum = 295;BA.debugLine="Log(\"Error leyendo area_name: \" & LastExceptio";
anywheresoftware.b4a.keywords.Common.LogImpl("73997900","Error leyendo area_name: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 299;BA.debugLine="If title = \"\" Then";
if ((_title).equals("")) { 
 //BA.debugLineNum = 300;BA.debugLine="If reportContent.ContainsKey(\"Id_Reporte\") Then";
if (_reportcontent.ContainsKey((Object)("Id_Reporte"))) { 
 //BA.debugLineNum = 301;BA.debugLine="title = \"Reporte \" & reportContent.Get(\"Id_Rep";
_title = "Reporte "+BA.ObjectToString(_reportcontent.Get((Object)("Id_Reporte")));
 }else if(_reportcontent.ContainsKey((Object)("id"))) { 
 //BA.debugLineNum = 303;BA.debugLine="title = \"Reporte \" & reportContent.Get(\"id\")";
_title = "Reporte "+BA.ObjectToString(_reportcontent.Get((Object)("id")));
 }else {
 //BA.debugLineNum = 305;BA.debugLine="title = \"Reporte \" & (i + 1)";
_title = "Reporte "+BA.NumberToString((_i+1));
 };
 };
 //BA.debugLineNum = 309;BA.debugLine="lv.AddSingleLine2(title, i)";
mostCurrent._lv.AddSingleLine2(BA.ObjectToCharSequence(_title),(Object)(_i));
 //BA.debugLineNum = 310;BA.debugLine="reportsMap.Put(i, reportContent)";
mostCurrent._reportsmap.Put((Object)(_i),(Object)(_reportcontent.getObject()));
 }
};
 //BA.debugLineNum = 313;BA.debugLine="If lv.Size = 0 Then ToastMessageShow(\"No se encon";
if (mostCurrent._lv.getSize()==0) { 
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se encontraron reportes para el área."),anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 314;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 315;BA.debugLine="End Sub";
return "";
}
public static String  _lv_itemclick(int _position,Object _value) throws Exception{
int _idx = 0;
anywheresoftware.b4a.objects.collections.Map _rep = null;
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _gen = null;
String _jsonstr = "";
 //BA.debugLineNum = 318;BA.debugLine="Sub lv_ItemClick (Position As Int, Value As Object";
 //BA.debugLineNum = 319;BA.debugLine="Dim idx As Int = Value";
_idx = (int)(BA.ObjectToNumber(_value));
 //BA.debugLineNum = 320;BA.debugLine="Dim rep As Map = reportsMap.Get(idx)";
_rep = new anywheresoftware.b4a.objects.collections.Map();
_rep = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._reportsmap.Get((Object)(_idx))));
 //BA.debugLineNum = 321;BA.debugLine="If rep = Null Then";
if (_rep== null) { 
 //BA.debugLineNum = 322;BA.debugLine="MsgboxAsync(\"Reporte inválido\", \"Error\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Reporte inválido"),BA.ObjectToCharSequence("Error"),processBA);
 //BA.debugLineNum = 323;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 326;BA.debugLine="Try";
try { //BA.debugLineNum = 327;BA.debugLine="Dim gen As JSONGenerator";
_gen = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 328;BA.debugLine="gen.Initialize(rep)";
_gen.Initialize(_rep);
 //BA.debugLineNum = 329;BA.debugLine="Dim jsonStr As String = gen.ToString";
_jsonstr = _gen.ToString();
 //BA.debugLineNum = 330;BA.debugLine="File.WriteString(File.DirInternal, \"current_repo";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json",_jsonstr);
 } 
       catch (Exception e13) {
			processBA.setLastException(e13); //BA.debugLineNum = 332;BA.debugLine="Log(\"Error writing current_report.json: \" & Last";
anywheresoftware.b4a.keywords.Common.LogImpl("74063246","Error writing current_report.json: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 333;BA.debugLine="MsgboxAsync(\"No se pudo preparar el detalle del";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("No se pudo preparar el detalle del reporte."),BA.ObjectToCharSequence("Error"),processBA);
 //BA.debugLineNum = 334;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 337;BA.debugLine="StartActivity(ReportDetail) ' ReportDetail debe e";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._reportdetail.getObject()));
 //BA.debugLineNum = 338;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="Public AreaToShow As String";
_areatoshow = "";
 //BA.debugLineNum = 17;BA.debugLine="Public BaseUrlToUse As String";
_baseurltouse = "";
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
}
