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

public class reportsbyuser extends Activity implements B4AActivity{
	public static reportsbyuser mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.reportsbyuser");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (reportsbyuser).");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.reportsbyuser");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.reportsbyuser", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (reportsbyuser) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (reportsbyuser) Resume **");
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
		return reportsbyuser.class;
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
            BA.LogInfo("** Activity (reportsbyuser) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (reportsbyuser) Pause event (activity is not paused). **");
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
            reportsbyuser mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (reportsbyuser) Resume **");
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
public static String _usertoshow = "";
public static String _baseurltouse = "";
public anywheresoftware.b4a.objects.ListViewWrapper _lv = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnrefresh = null;
public anywheresoftware.b4a.objects.collections.Map _reportsmap = null;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.menuprincipal _menuprincipal = null;
public b4a.example.reportsbyarea _reportsbyarea = null;
public b4a.example.reportdetail _reportdetail = null;
public b4a.example.menuprincipal_maquilas _menuprincipal_maquilas = null;
public b4a.example.login _login = null;
public b4a.example.reportdialog _reportdialog = null;
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
 //BA.debugLineNum = 20;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 21;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 22;BA.debugLine="Dim screenW As Int = 100%x";
_screenw = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 24;BA.debugLine="lblTitle.Initialize(\"lblTitle\")";
mostCurrent._lbltitle.Initialize(mostCurrent.activityBA,"lblTitle");
 //BA.debugLineNum = 25;BA.debugLine="lblTitle.Text = \"Mis Reportes\"";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence("Mis Reportes"));
 //BA.debugLineNum = 26;BA.debugLine="lblTitle.TextSize = 16";
mostCurrent._lbltitle.setTextSize((float) (16));
 //BA.debugLineNum = 27;BA.debugLine="lblTitle.Gravity = Gravity.CENTER_HORIZONTAL + Gr";
mostCurrent._lbltitle.setGravity((int) (anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL+anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL));
 //BA.debugLineNum = 28;BA.debugLine="Activity.AddView(lblTitle, 0, 0, screenW, 10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lbltitle.getObject()),(int) (0),(int) (0),_screenw,anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 30;BA.debugLine="btnRefresh.Initialize(\"btnRefresh\")";
mostCurrent._btnrefresh.Initialize(mostCurrent.activityBA,"btnRefresh");
 //BA.debugLineNum = 31;BA.debugLine="btnRefresh.Text = \"Refrescar\"";
mostCurrent._btnrefresh.setText(BA.ObjectToCharSequence("Refrescar"));
 //BA.debugLineNum = 32;BA.debugLine="Activity.AddView(btnRefresh, 2%x, 10%y + 4dip, 30";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._btnrefresh.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA)+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (8),mostCurrent.activityBA));
 //BA.debugLineNum = 34;BA.debugLine="lv.Initialize(\"lvReports\")";
mostCurrent._lv.Initialize(mostCurrent.activityBA,"lvReports");
 //BA.debugLineNum = 35;BA.debugLine="lv.SingleLineLayout.Label.TextColor = Colors.Blac";
mostCurrent._lv.getSingleLineLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 36;BA.debugLine="Activity.AddView(lv, 0, 18%y, screenW, 72%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lv.getObject()),(int) (0),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (18),mostCurrent.activityBA),_screenw,anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (72),mostCurrent.activityBA));
 //BA.debugLineNum = 38;BA.debugLine="reportsMap.Initialize";
mostCurrent._reportsmap.Initialize();
 //BA.debugLineNum = 40;BA.debugLine="If UserToShow <> \"\" Then";
if ((_usertoshow).equals("") == false) { 
 //BA.debugLineNum = 41;BA.debugLine="lblTitle.Text = \"Reportes de Usuario: \" & UserTo";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence("Reportes de Usuario: "+_usertoshow));
 //BA.debugLineNum = 42;BA.debugLine="If BaseUrlToUse = \"\" Then";
if ((_baseurltouse).equals("")) { 
 //BA.debugLineNum = 43;BA.debugLine="BaseUrlToUse = \"https://humane-pelican-briefly.";
_baseurltouse = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api/get_reports_by_user.php?user={user}";
 };
 //BA.debugLineNum = 45;BA.debugLine="FetchReports(BaseUrlToUse, UserToShow)";
_fetchreports(_baseurltouse,_usertoshow);
 }else {
 //BA.debugLineNum = 48;BA.debugLine="Try";
try { //BA.debugLineNum = 49;BA.debugLine="If Starter.Id_Usuario <> \"\" Then";
if (mostCurrent._starter._id_usuario /*int*/ !=(double)(Double.parseDouble(""))) { 
 //BA.debugLineNum = 50;BA.debugLine="UserToShow = Starter.Id_Usuario";
_usertoshow = BA.NumberToString(mostCurrent._starter._id_usuario /*int*/ );
 //BA.debugLineNum = 51;BA.debugLine="lblTitle.Text = \"Mis Reportes\"";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence("Mis Reportes"));
 //BA.debugLineNum = 52;BA.debugLine="If BaseUrlToUse = \"\" Then";
if ((_baseurltouse).equals("")) { 
 //BA.debugLineNum = 53;BA.debugLine="BaseUrlToUse = \"https://humane-pelican-briefl";
_baseurltouse = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api/get_reports_by_user.php?user={user}";
 };
 //BA.debugLineNum = 55;BA.debugLine="FetchReports(BaseUrlToUse, UserToShow)";
_fetchreports(_baseurltouse,_usertoshow);
 };
 } 
       catch (Exception e32) {
			processBA.setLastException(e32); //BA.debugLineNum = 58;BA.debugLine="Log(\"Error getting Starter.Id_Usuario\")";
anywheresoftware.b4a.keywords.Common.LogImpl("55636134","Error getting Starter.Id_Usuario",0);
 };
 };
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 67;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 69;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 63;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 65;BA.debugLine="End Sub";
return "";
}
public static String  _btnrefresh_click() throws Exception{
 //BA.debugLineNum = 71;BA.debugLine="Sub btnRefresh_Click";
 //BA.debugLineNum = 72;BA.debugLine="If UserToShow <> \"\" And BaseUrlToUse <> \"\" Then";
if ((_usertoshow).equals("") == false && (_baseurltouse).equals("") == false) { 
 //BA.debugLineNum = 73;BA.debugLine="FetchReports(BaseUrlToUse, UserToShow)";
_fetchreports(_baseurltouse,_usertoshow);
 }else {
 //BA.debugLineNum = 75;BA.debugLine="ToastMessageShow(\"No hay usuario definido.\", Tru";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No hay usuario definido."),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 77;BA.debugLine="End Sub";
return "";
}
public static String  _fetchreports(String _baseurl,String _userid) throws Exception{
String _url = "";
b4a.example.httpjob _j = null;
 //BA.debugLineNum = 79;BA.debugLine="Sub FetchReports(BaseUrl As String, UserId As Stri";
 //BA.debugLineNum = 80;BA.debugLine="Dim url As String = BaseUrl";
_url = _baseurl;
 //BA.debugLineNum = 81;BA.debugLine="If url.Contains(\"{user}\") Then";
if (_url.contains("{user}")) { 
 //BA.debugLineNum = 82;BA.debugLine="url = url.Replace(\"{user}\", UserId)";
_url = _url.replace("{user}",_userid);
 }else {
 //BA.debugLineNum = 84;BA.debugLine="If url.Contains(\"?\") Then";
if (_url.contains("?")) { 
 //BA.debugLineNum = 85;BA.debugLine="url = url & \"&user=\" & UserId";
_url = _url+"&user="+_userid;
 }else {
 //BA.debugLineNum = 87;BA.debugLine="url = url & \"?user=\" & UserId";
_url = _url+"?user="+_userid;
 };
 };
 //BA.debugLineNum = 91;BA.debugLine="ProgressDialogShow(\"Cargando mis reportes...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Cargando mis reportes..."));
 //BA.debugLineNum = 92;BA.debugLine="Dim j As HttpJob";
_j = new b4a.example.httpjob();
 //BA.debugLineNum = 93;BA.debugLine="j.Initialize(\"getReports\", Me)";
_j._initialize /*String*/ (processBA,"getReports",reportsbyuser.getObject());
 //BA.debugLineNum = 94;BA.debugLine="j.Download(url)";
_j._download /*String*/ (_url);
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 13;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 14;BA.debugLine="Private lv As ListView";
mostCurrent._lv = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Private lblTitle As Label";
mostCurrent._lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Private btnRefresh As Button";
mostCurrent._btnrefresh = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Private reportsMap As Map";
mostCurrent._reportsmap = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(b4a.example.httpjob _job) throws Exception{
String _res = "";
int _p = 0;
int _p2 = 0;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
Object _root = null;
anywheresoftware.b4a.objects.collections.List _list = null;
anywheresoftware.b4a.objects.collections.Map _m = null;
Object _d = null;
int _i = 0;
anywheresoftware.b4a.objects.collections.Map _item = null;
String _title = "";
String _areaname = "";
String _s = "";
anywheresoftware.b4a.objects.collections.JSONParser _jp2 = null;
anywheresoftware.b4a.objects.collections.Map _mr = null;
anywheresoftware.b4a.objects.collections.Map _ma = null;
 //BA.debugLineNum = 97;BA.debugLine="Sub JobDone(Job As HttpJob)";
 //BA.debugLineNum = 98;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 99;BA.debugLine="If Job.Success = False Then";
if (_job._success /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 100;BA.debugLine="ToastMessageShow(\"Error: \" & Job.ErrorMessage, T";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error: "+_job._errormessage /*String*/ ),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 101;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 102;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 105;BA.debugLine="Dim res As String = Job.GetString";
_res = _job._getstring /*String*/ ();
 //BA.debugLineNum = 109;BA.debugLine="Dim p As Int = res.IndexOf(\"{\")";
_p = _res.indexOf("{");
 //BA.debugLineNum = 110;BA.debugLine="Dim p2 As Int = res.IndexOf(\"[\")";
_p2 = _res.indexOf("[");
 //BA.debugLineNum = 111;BA.debugLine="If p = -1 And p2 = -1 Then";
if (_p==-1 && _p2==-1) { 
 //BA.debugLineNum = 112;BA.debugLine="ToastMessageShow(\"Respuesta inválida\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Respuesta inválida"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 113;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 114;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 116;BA.debugLine="If p > -1 And (p2 = -1 Or p < p2) Then res = res.";
if (_p>-1 && (_p2==-1 || _p<_p2)) { 
_res = _res.substring(_p);}
else {
_res = _res.substring(_p2);};
 //BA.debugLineNum = 118;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 119;BA.debugLine="parser.Initialize(res)";
_parser.Initialize(_res);
 //BA.debugLineNum = 120;BA.debugLine="Dim root As Object";
_root = new Object();
 //BA.debugLineNum = 121;BA.debugLine="Try";
try { //BA.debugLineNum = 122;BA.debugLine="If res.StartsWith(\"[\") Then";
if (_res.startsWith("[")) { 
 //BA.debugLineNum = 123;BA.debugLine="root = parser.NextArray";
_root = (Object)(_parser.NextArray().getObject());
 }else {
 //BA.debugLineNum = 125;BA.debugLine="root = parser.NextObject";
_root = (Object)(_parser.NextObject().getObject());
 };
 } 
       catch (Exception e26) {
			processBA.setLastException(e26); //BA.debugLineNum = 128;BA.debugLine="ToastMessageShow(\"Error JSON\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error JSON"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 129;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 130;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 133;BA.debugLine="Dim list As List";
_list = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 134;BA.debugLine="list.Initialize";
_list.Initialize();
 //BA.debugLineNum = 136;BA.debugLine="If root Is Map Then";
if (_root instanceof java.util.Map) { 
 //BA.debugLineNum = 137;BA.debugLine="Dim m As Map = root";
_m = new anywheresoftware.b4a.objects.collections.Map();
_m = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_root));
 //BA.debugLineNum = 138;BA.debugLine="If m.ContainsKey(\"data\") Then";
if (_m.ContainsKey((Object)("data"))) { 
 //BA.debugLineNum = 139;BA.debugLine="Dim d As Object = m.Get(\"data\")";
_d = _m.Get((Object)("data"));
 //BA.debugLineNum = 140;BA.debugLine="If d Is List Then list = d";
if (_d instanceof java.util.List) { 
_list = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_d));};
 }else if(_m.ContainsKey((Object)("success")) && (_m.Get((Object)("success"))).equals((Object)(anywheresoftware.b4a.keywords.Common.True))) { 
 };
 }else if(_root instanceof java.util.List) { 
 //BA.debugLineNum = 145;BA.debugLine="list = root";
_list = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root));
 };
 //BA.debugLineNum = 148;BA.debugLine="reportsMap.Clear";
mostCurrent._reportsmap.Clear();
 //BA.debugLineNum = 149;BA.debugLine="lv.Clear";
mostCurrent._lv.Clear();
 //BA.debugLineNum = 151;BA.debugLine="For i = 0 To list.Size - 1";
{
final int step44 = 1;
final int limit44 = (int) (_list.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit44 ;_i = _i + step44 ) {
 //BA.debugLineNum = 152;BA.debugLine="Dim item As Map = list.Get(i)";
_item = new anywheresoftware.b4a.objects.collections.Map();
_item = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_list.Get(_i)));
 //BA.debugLineNum = 155;BA.debugLine="Dim title As String = \"\"";
_title = "";
 //BA.debugLineNum = 156;BA.debugLine="If item.ContainsKey(\"FechaRegistro_Reporte\") The";
if (_item.ContainsKey((Object)("FechaRegistro_Reporte"))) { 
_title = BA.ObjectToString(_item.Get((Object)("FechaRegistro_Reporte")));};
 //BA.debugLineNum = 159;BA.debugLine="Dim areaName As String = \"\"";
_areaname = "";
 //BA.debugLineNum = 160;BA.debugLine="If item.ContainsKey(\"Nombre_Area\") Then";
if (_item.ContainsKey((Object)("Nombre_Area"))) { 
 //BA.debugLineNum = 161;BA.debugLine="areaName = item.Get(\"Nombre_Area\")";
_areaname = BA.ObjectToString(_item.Get((Object)("Nombre_Area")));
 }else if(_item.ContainsKey((Object)("JSON_Reporte"))) { 
 //BA.debugLineNum = 163;BA.debugLine="Dim s As String = item.Get(\"JSON_Reporte\")";
_s = BA.ObjectToString(_item.Get((Object)("JSON_Reporte")));
 //BA.debugLineNum = 164;BA.debugLine="Try";
try { //BA.debugLineNum = 165;BA.debugLine="Dim jp2 As JSONParser";
_jp2 = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 166;BA.debugLine="jp2.Initialize(s)";
_jp2.Initialize(_s);
 //BA.debugLineNum = 167;BA.debugLine="Dim mr As Map = jp2.NextObject";
_mr = new anywheresoftware.b4a.objects.collections.Map();
_mr = _jp2.NextObject();
 //BA.debugLineNum = 168;BA.debugLine="If mr.ContainsKey(\"area\") Then";
if (_mr.ContainsKey((Object)("area"))) { 
 //BA.debugLineNum = 169;BA.debugLine="Dim ma As Map = mr.Get(\"area\")";
_ma = new anywheresoftware.b4a.objects.collections.Map();
_ma = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_mr.Get((Object)("area"))));
 //BA.debugLineNum = 170;BA.debugLine="If ma.ContainsKey(\"area_name\") Then areaName";
if (_ma.ContainsKey((Object)("area_name"))) { 
_areaname = BA.ObjectToString(_ma.Get((Object)("area_name")));};
 };
 } 
       catch (Exception e62) {
			processBA.setLastException(e62); //BA.debugLineNum = 173;BA.debugLine="Log(LastException.Message)";
anywheresoftware.b4a.keywords.Common.LogImpl("55963852",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 177;BA.debugLine="If areaName <> \"\" Then title = title & \" - \" & a";
if ((_areaname).equals("") == false) { 
_title = _title+" - "+_areaname;};
 //BA.debugLineNum = 179;BA.debugLine="lv.AddSingleLine2(title, i)";
mostCurrent._lv.AddSingleLine2(BA.ObjectToCharSequence(_title),(Object)(_i));
 //BA.debugLineNum = 180;BA.debugLine="reportsMap.Put(i, item)";
mostCurrent._reportsmap.Put((Object)(_i),(Object)(_item.getObject()));
 }
};
 //BA.debugLineNum = 183;BA.debugLine="If lv.Size = 0 Then ToastMessageShow(\"No tienes r";
if (mostCurrent._lv.getSize()==0) { 
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No tienes reportes."),anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 184;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 185;BA.debugLine="End Sub";
return "";
}
public static String  _lvreports_itemclick(int _position,Object _value) throws Exception{
int _idx = 0;
anywheresoftware.b4a.objects.collections.Map _item = null;
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _jg = null;
 //BA.debugLineNum = 187;BA.debugLine="Sub lvReports_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 188;BA.debugLine="Dim idx As Int = Value";
_idx = (int)(BA.ObjectToNumber(_value));
 //BA.debugLineNum = 189;BA.debugLine="Dim item As Map = reportsMap.Get(idx)";
_item = new anywheresoftware.b4a.objects.collections.Map();
_item = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._reportsmap.Get((Object)(_idx))));
 //BA.debugLineNum = 192;BA.debugLine="Dim jg As JSONGenerator";
_jg = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 193;BA.debugLine="jg.Initialize(item)";
_jg.Initialize(_item);
 //BA.debugLineNum = 194;BA.debugLine="File.WriteString(File.DirInternal, \"current_repor";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json",_jg.ToString());
 //BA.debugLineNum = 196;BA.debugLine="ReportDetail.AllowEdit = True";
mostCurrent._reportdetail._allowedit /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 197;BA.debugLine="StartActivity(ReportDetail)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._reportdetail.getObject()));
 //BA.debugLineNum = 198;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 8;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Public UserToShow As String";
_usertoshow = "";
 //BA.debugLineNum = 10;BA.debugLine="Public BaseUrlToUse As String";
_baseurltouse = "";
 //BA.debugLineNum = 11;BA.debugLine="End Sub";
return "";
}
}
