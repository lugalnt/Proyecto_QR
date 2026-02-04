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
public b4a.example.reportdetail _reportdetail = null;
public b4a.example.menuprincipal_maquilas _menuprincipal_maquilas = null;
public b4a.example.login _login = null;
public b4a.example.reportdialog _reportdialog = null;
public b4a.example.reportsbyuser _reportsbyuser = null;
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
 //BA.debugLineNum = 42;BA.debugLine="lv.SingleLineLayout.Label.TextColor = Colors.Blac";
mostCurrent._lv.getSingleLineLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 43;BA.debugLine="Activity.AddView(lv, 0, 18%y, screenW, 72%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lv.getObject()),(int) (0),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (18),mostCurrent.activityBA),_screenw,anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (72),mostCurrent.activityBA));
 //BA.debugLineNum = 45;BA.debugLine="reportsMap.Initialize";
mostCurrent._reportsmap.Initialize();
 //BA.debugLineNum = 47;BA.debugLine="If AreaToShow <> \"\" Then";
if ((_areatoshow).equals("") == false) { 
 //BA.debugLineNum = 48;BA.debugLine="lblTitle.Text = \"Reportes del área: \" & AreaToSh";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence("Reportes del área: "+_areatoshow));
 //BA.debugLineNum = 49;BA.debugLine="If BaseUrlToUse = \"\" Then";
if ((_baseurltouse).equals("")) { 
 //BA.debugLineNum = 50;BA.debugLine="BaseUrlToUse = \"https://yourserver.example/api/";
_baseurltouse = "https://yourserver.example/api/get_reports_by_area.php?area={area}";
 };
 //BA.debugLineNum = 52;BA.debugLine="FetchReports(BaseUrlToUse, AreaToShow)";
_fetchreports(_baseurltouse,_areatoshow);
 };
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 60;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 56;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
public static String  _btnrefresh_click() throws Exception{
 //BA.debugLineNum = 64;BA.debugLine="Sub btnRefresh_Click";
 //BA.debugLineNum = 65;BA.debugLine="If AreaToShow = \"\" Or BaseUrlToUse = \"\" Then";
if ((_areatoshow).equals("") || (_baseurltouse).equals("")) { 
 //BA.debugLineNum = 66;BA.debugLine="ToastMessageShow(\"Asigne AreaToShow y BaseUrlToU";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Asigne AreaToShow y BaseUrlToUse antes de iniciar."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 67;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 69;BA.debugLine="FetchReports(BaseUrlToUse, AreaToShow)";
_fetchreports(_baseurltouse,_areatoshow);
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
public static String  _extractfirstjson(String _s) throws Exception{
int _len = 0;
int _firstobj = 0;
int _firstarr = 0;
int _start = 0;
int _depth = 0;
boolean _instring = false;
boolean _escaped = false;
int _i = 0;
String _ch = "";
 //BA.debugLineNum = 397;BA.debugLine="Sub ExtractFirstJson(s As String) As String";
 //BA.debugLineNum = 398;BA.debugLine="If s = Null Then Return \"\"";
if (_s== null) { 
if (true) return "";};
 //BA.debugLineNum = 399;BA.debugLine="s = s.Trim";
_s = _s.trim();
 //BA.debugLineNum = 400;BA.debugLine="Dim len As Int = s.Length";
_len = _s.length();
 //BA.debugLineNum = 401;BA.debugLine="If len = 0 Then Return \"\"";
if (_len==0) { 
if (true) return "";};
 //BA.debugLineNum = 404;BA.debugLine="Dim firstObj As Int = s.IndexOf(\"{\")";
_firstobj = _s.indexOf("{");
 //BA.debugLineNum = 405;BA.debugLine="Dim firstArr As Int = s.IndexOf(\"[\")";
_firstarr = _s.indexOf("[");
 //BA.debugLineNum = 406;BA.debugLine="Dim start As Int";
_start = 0;
 //BA.debugLineNum = 407;BA.debugLine="If firstObj = -1 And firstArr = -1 Then Return \"\"";
if (_firstobj==-1 && _firstarr==-1) { 
if (true) return "";};
 //BA.debugLineNum = 408;BA.debugLine="If firstObj = -1 Then";
if (_firstobj==-1) { 
 //BA.debugLineNum = 409;BA.debugLine="start = firstArr";
_start = _firstarr;
 }else if(_firstarr==-1) { 
 //BA.debugLineNum = 411;BA.debugLine="start = firstObj";
_start = _firstobj;
 }else {
 //BA.debugLineNum = 413;BA.debugLine="If firstObj < firstArr Then";
if (_firstobj<_firstarr) { 
 //BA.debugLineNum = 414;BA.debugLine="start = firstObj";
_start = _firstobj;
 }else {
 //BA.debugLineNum = 416;BA.debugLine="start = firstArr";
_start = _firstarr;
 };
 };
 //BA.debugLineNum = 420;BA.debugLine="Dim depth As Int = 0";
_depth = (int) (0);
 //BA.debugLineNum = 421;BA.debugLine="Dim inString As Boolean = False";
_instring = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 422;BA.debugLine="Dim escaped As Boolean = False";
_escaped = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 424;BA.debugLine="For i = start To len - 1";
{
final int step23 = 1;
final int limit23 = (int) (_len-1);
_i = _start ;
for (;_i <= limit23 ;_i = _i + step23 ) {
 //BA.debugLineNum = 425;BA.debugLine="Dim ch As String = s.SubString2(i, i + 1)";
_ch = _s.substring(_i,(int) (_i+1));
 //BA.debugLineNum = 426;BA.debugLine="If escaped Then";
if (_escaped) { 
 //BA.debugLineNum = 427;BA.debugLine="escaped = False";
_escaped = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 428;BA.debugLine="Continue";
if (true) continue;
 };
 //BA.debugLineNum = 430;BA.debugLine="If ch = \"\\\" Then";
if ((_ch).equals("\\")) { 
 //BA.debugLineNum = 431;BA.debugLine="escaped = True";
_escaped = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 432;BA.debugLine="Continue";
if (true) continue;
 };
 //BA.debugLineNum = 434;BA.debugLine="If ch = \"\"\"\" Then";
if ((_ch).equals("\"")) { 
 //BA.debugLineNum = 435;BA.debugLine="inString = Not(inString)";
_instring = anywheresoftware.b4a.keywords.Common.Not(_instring);
 //BA.debugLineNum = 436;BA.debugLine="Continue";
if (true) continue;
 };
 //BA.debugLineNum = 439;BA.debugLine="If Not(inString) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_instring)) { 
 //BA.debugLineNum = 440;BA.debugLine="If ch = \"{\" Or ch = \"[\" Then";
if ((_ch).equals("{") || (_ch).equals("[")) { 
 //BA.debugLineNum = 441;BA.debugLine="depth = depth + 1";
_depth = (int) (_depth+1);
 }else if((_ch).equals("}") || (_ch).equals("]")) { 
 //BA.debugLineNum = 443;BA.debugLine="depth = depth - 1";
_depth = (int) (_depth-1);
 //BA.debugLineNum = 444;BA.debugLine="If depth = 0 Then";
if (_depth==0) { 
 //BA.debugLineNum = 446;BA.debugLine="Return s.SubString2(start, i + 1)";
if (true) return _s.substring(_start,(int) (_i+1));
 };
 };
 };
 }
};
 //BA.debugLineNum = 452;BA.debugLine="Return \"\" ' no se encontró un bloque completo";
if (true) return "";
 //BA.debugLineNum = 453;BA.debugLine="End Sub";
return "";
}
public static String  _fetchreports(String _baseurl,String _areacode) throws Exception{
String _url = "";
b4a.example.httpjob _j = null;
 //BA.debugLineNum = 73;BA.debugLine="Sub FetchReports(BaseUrl As String, AreaCode As St";
 //BA.debugLineNum = 74;BA.debugLine="Dim url As String = BaseUrl";
_url = _baseurl;
 //BA.debugLineNum = 75;BA.debugLine="If url.Contains(\"{area}\") Then";
if (_url.contains("{area}")) { 
 //BA.debugLineNum = 76;BA.debugLine="url = url.Replace(\"{area}\", AreaCode)";
_url = _url.replace("{area}",_areacode);
 }else {
 //BA.debugLineNum = 78;BA.debugLine="If url.Contains(\"?\") Then";
if (_url.contains("?")) { 
 //BA.debugLineNum = 79;BA.debugLine="url = url & \"&area=\" & AreaCode";
_url = _url+"&area="+_areacode;
 }else {
 //BA.debugLineNum = 81;BA.debugLine="url = url & \"?area=\" & AreaCode";
_url = _url+"?area="+_areacode;
 };
 };
 //BA.debugLineNum = 86;BA.debugLine="If Starter.Is_Maquila Then";
if (mostCurrent._starter._is_maquila /*boolean*/ ) { 
 //BA.debugLineNum = 87;BA.debugLine="url = url & \"&id_maquila=\" & Starter.Id_Ma";
_url = _url+"&id_maquila="+BA.NumberToString(mostCurrent._starter._id_maquila /*int*/ );
 };
 //BA.debugLineNum = 91;BA.debugLine="ProgressDialogShow(\"Cargando reportes...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Cargando reportes..."));
 //BA.debugLineNum = 92;BA.debugLine="Dim j As HttpJob";
_j = new b4a.example.httpjob();
 //BA.debugLineNum = 93;BA.debugLine="j.Initialize(\"getReports\", Me)";
_j._initialize /*String*/ (processBA,"getReports",reportsbyarea.getObject());
 //BA.debugLineNum = 94;BA.debugLine="j.Download(url)";
_j._download /*String*/ (_url);
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
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
String _extracted = "";
anywheresoftware.b4a.keywords.StringBuilderWrapper _sb = null;
int _i = 0;
String _ch = "";
byte[] _b = null;
int _code = 0;
String _cleaned = "";
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
Object _root = null;
String _snippet = "";
anywheresoftware.b4a.objects.collections.List _list = null;
anywheresoftware.b4a.objects.collections.Map _mp = null;
String _k = "";
Object _v = null;
Object _rawitem = null;
anywheresoftware.b4a.objects.collections.Map _reportcontent = null;
String _datestr = "";
anywheresoftware.b4a.objects.collections.Map _rm = null;
anywheresoftware.b4a.objects.collections.Map _candidate = null;
Object _parsedobj = null;
anywheresoftware.b4a.objects.collections.List _pl = null;
String _sjson = "";
anywheresoftware.b4a.objects.collections.JSONParser _p = null;
Object _maybe = null;
anywheresoftware.b4a.objects.collections.List _ltmp = null;
String _s2 = "";
anywheresoftware.b4a.objects.collections.JSONParser _p3 = null;
Object _maybe2 = null;
String _title = "";
anywheresoftware.b4a.objects.collections.Map _areamap = null;
anywheresoftware.b4a.objects.collections.Map _stored = null;
 //BA.debugLineNum = 100;BA.debugLine="Sub JobDone(Job As HttpJob)";
 //BA.debugLineNum = 101;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 102;BA.debugLine="If Job.Success = False Then";
if (_job._success /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 103;BA.debugLine="ToastMessageShow(\"Error al obtener reportes: \" &";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error al obtener reportes: "+_job._errormessage /*String*/ ),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 104;BA.debugLine="Log(\"JobDone: HTTP error: \" & Job.ErrorMessage)";
anywheresoftware.b4a.keywords.Common.LogImpl("01900548","JobDone: HTTP error: "+_job._errormessage /*String*/ ,0);
 //BA.debugLineNum = 105;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 106;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 109;BA.debugLine="Dim res As String = Job.GetString";
_res = _job._getstring /*String*/ ();
 //BA.debugLineNum = 110;BA.debugLine="If res = Null Then res = \"\"";
if (_res== null) { 
_res = "";};
 //BA.debugLineNum = 112;BA.debugLine="Try";
try { //BA.debugLineNum = 113;BA.debugLine="res = res.Replace(Chr(65279), \"\")";
_res = _res.replace(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (65279))),"");
 } 
       catch (Exception e13) {
			processBA.setLastException(e13); //BA.debugLineNum = 115;BA.debugLine="Log(\"BOM replace error: \" & LastException.Messag";
anywheresoftware.b4a.keywords.Common.LogImpl("01900559","BOM replace error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 117;BA.debugLine="res = res.Trim";
_res = _res.trim();
 //BA.debugLineNum = 119;BA.debugLine="If res = \"\" Then";
if ((_res).equals("")) { 
 //BA.debugLineNum = 120;BA.debugLine="ToastMessageShow(\"Respuesta vacía del servidor.\"";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Respuesta vacía del servidor."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 121;BA.debugLine="Log(\"JobDone: empty response\")";
anywheresoftware.b4a.keywords.Common.LogImpl("01900565","JobDone: empty response",0);
 //BA.debugLineNum = 122;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 123;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 127;BA.debugLine="Try";
try { //BA.debugLineNum = 128;BA.debugLine="File.WriteString(File.DirInternal, \"reports_resp";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"reports_response_debug.json",_res);
 //BA.debugLineNum = 129;BA.debugLine="Log(\"Saved raw response to: \" & File.Combine(Fil";
anywheresoftware.b4a.keywords.Common.LogImpl("01900573","Saved raw response to: "+anywheresoftware.b4a.keywords.Common.File.Combine(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"reports_response_debug.json"),0);
 } 
       catch (Exception e26) {
			processBA.setLastException(e26); //BA.debugLineNum = 131;BA.debugLine="Log(\"Error writing debug file: \" & LastException";
anywheresoftware.b4a.keywords.Common.LogImpl("01900575","Error writing debug file: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 135;BA.debugLine="Dim extracted As String = ExtractFirstJson(res)";
_extracted = _extractfirstjson(_res);
 //BA.debugLineNum = 136;BA.debugLine="If extracted = \"\" Then";
if ((_extracted).equals("")) { 
 //BA.debugLineNum = 137;BA.debugLine="Log(\"ExtractFirstJson returned empty. Response m";
anywheresoftware.b4a.keywords.Common.LogImpl("01900581","ExtractFirstJson returned empty. Response may be malformed. Response snippet: "+_res.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(_res.length(),1000))),0);
 //BA.debugLineNum = 138;BA.debugLine="ToastMessageShow(\"No se detectó un bloque JSON c";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se detectó un bloque JSON completo en la respuesta (ver logs)."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 139;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 140;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 144;BA.debugLine="Try";
try { //BA.debugLineNum = 145;BA.debugLine="File.WriteString(File.DirInternal, \"reports_resp";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"reports_response_extracted.json",_extracted);
 //BA.debugLineNum = 146;BA.debugLine="Log(\"Saved extracted JSON to: \" & File.Combine(F";
anywheresoftware.b4a.keywords.Common.LogImpl("01900590","Saved extracted JSON to: "+anywheresoftware.b4a.keywords.Common.File.Combine(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"reports_response_extracted.json"),0);
 } 
       catch (Exception e39) {
			processBA.setLastException(e39); //BA.debugLineNum = 148;BA.debugLine="Log(\"Error writing extracted debug file: \" & Las";
anywheresoftware.b4a.keywords.Common.LogImpl("01900592","Error writing extracted debug file: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 152;BA.debugLine="Dim sb As StringBuilder";
_sb = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 153;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 154;BA.debugLine="For i = 0 To extracted.Length - 1";
{
final int step43 = 1;
final int limit43 = (int) (_extracted.length()-1);
_i = (int) (0) ;
for (;_i <= limit43 ;_i = _i + step43 ) {
 //BA.debugLineNum = 155;BA.debugLine="Dim ch As String = extracted.SubString2(i, i + 1";
_ch = _extracted.substring(_i,(int) (_i+1));
 //BA.debugLineNum = 156;BA.debugLine="Dim b() As Byte = ch.GetBytes(\"UTF8\")";
_b = _ch.getBytes("UTF8");
 //BA.debugLineNum = 157;BA.debugLine="Dim code As Int = 32";
_code = (int) (32);
 //BA.debugLineNum = 158;BA.debugLine="If b.Length > 0 Then";
if (_b.length>0) { 
 //BA.debugLineNum = 159;BA.debugLine="code = b(0)";
_code = (int) (_b[(int) (0)]);
 //BA.debugLineNum = 160;BA.debugLine="If code < 0 Then code = code + 256";
if (_code<0) { 
_code = (int) (_code+256);};
 };
 //BA.debugLineNum = 162;BA.debugLine="If code >= 32 Or code = 9 Or code = 10 Or code =";
if (_code>=32 || _code==9 || _code==10 || _code==13) { 
 //BA.debugLineNum = 163;BA.debugLine="sb.Append(ch)";
_sb.Append(_ch);
 }else {
 };
 }
};
 //BA.debugLineNum = 168;BA.debugLine="Dim cleaned As String = sb.ToString";
_cleaned = _sb.ToString();
 //BA.debugLineNum = 170;BA.debugLine="If cleaned <> extracted Then";
if ((_cleaned).equals(_extracted) == false) { 
 //BA.debugLineNum = 171;BA.debugLine="Log(\"EXTRACTED cleaned: length from \" & extracte";
anywheresoftware.b4a.keywords.Common.LogImpl("01900615","EXTRACTED cleaned: length from "+BA.NumberToString(_extracted.length())+" -> "+BA.NumberToString(_cleaned.length()),0);
 };
 //BA.debugLineNum = 175;BA.debugLine="Try";
try { //BA.debugLineNum = 176;BA.debugLine="File.WriteString(File.DirInternal, \"reports_resp";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"reports_response_cleaned.json",_cleaned);
 //BA.debugLineNum = 177;BA.debugLine="Log(\"Saved cleaned JSON to: \" & File.Combine(Fil";
anywheresoftware.b4a.keywords.Common.LogImpl("01900621","Saved cleaned JSON to: "+anywheresoftware.b4a.keywords.Common.File.Combine(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"reports_response_cleaned.json"),0);
 } 
       catch (Exception e64) {
			processBA.setLastException(e64); //BA.debugLineNum = 179;BA.debugLine="Log(\"Could not write cleaned JSON file: \" & Last";
anywheresoftware.b4a.keywords.Common.LogImpl("01900623","Could not write cleaned JSON file: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 185;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 186;BA.debugLine="Dim root As Object";
_root = new Object();
 //BA.debugLineNum = 187;BA.debugLine="Try";
try { //BA.debugLineNum = 188;BA.debugLine="parser.Initialize(cleaned)";
_parser.Initialize(_cleaned);
 //BA.debugLineNum = 189;BA.debugLine="If cleaned.StartsWith(\"{\") Then";
if (_cleaned.startsWith("{")) { 
 //BA.debugLineNum = 190;BA.debugLine="Try";
try { //BA.debugLineNum = 191;BA.debugLine="root = parser.NextObject";
_root = (Object)(_parser.NextObject().getObject());
 //BA.debugLineNum = 192;BA.debugLine="Log(\"Parsed cleaned as JSON Object\")";
anywheresoftware.b4a.keywords.Common.LogImpl("01900636","Parsed cleaned as JSON Object",0);
 } 
       catch (Exception e75) {
			processBA.setLastException(e75); //BA.debugLineNum = 194;BA.debugLine="Log(\"Parser NextObject failed: \" & LastExcepti";
anywheresoftware.b4a.keywords.Common.LogImpl("01900638","Parser NextObject failed: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 195;BA.debugLine="root = Null";
_root = anywheresoftware.b4a.keywords.Common.Null;
 };
 }else if(_cleaned.startsWith("[")) { 
 //BA.debugLineNum = 198;BA.debugLine="Try";
try { //BA.debugLineNum = 199;BA.debugLine="root = parser.NextArray";
_root = (Object)(_parser.NextArray().getObject());
 //BA.debugLineNum = 200;BA.debugLine="Log(\"Parsed cleaned as JSON Array\")";
anywheresoftware.b4a.keywords.Common.LogImpl("01900644","Parsed cleaned as JSON Array",0);
 } 
       catch (Exception e83) {
			processBA.setLastException(e83); //BA.debugLineNum = 202;BA.debugLine="Log(\"Parser NextArray failed: \" & LastExceptio";
anywheresoftware.b4a.keywords.Common.LogImpl("01900646","Parser NextArray failed: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 203;BA.debugLine="root = Null";
_root = anywheresoftware.b4a.keywords.Common.Null;
 };
 }else {
 //BA.debugLineNum = 206;BA.debugLine="Log(\"Cleaned does not start with { or [, cannot";
anywheresoftware.b4a.keywords.Common.LogImpl("01900650","Cleaned does not start with { or [, cannot parse.",0);
 //BA.debugLineNum = 207;BA.debugLine="root = Null";
_root = anywheresoftware.b4a.keywords.Common.Null;
 };
 } 
       catch (Exception e91) {
			processBA.setLastException(e91); //BA.debugLineNum = 210;BA.debugLine="Log(\"Parser initialize error: \" & LastException.";
anywheresoftware.b4a.keywords.Common.LogImpl("01900654","Parser initialize error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 211;BA.debugLine="root = Null";
_root = anywheresoftware.b4a.keywords.Common.Null;
 };
 //BA.debugLineNum = 214;BA.debugLine="If root = Null Then";
if (_root== null) { 
 //BA.debugLineNum = 215;BA.debugLine="Try";
try { //BA.debugLineNum = 216;BA.debugLine="Dim snippet As String = cleaned";
_snippet = _cleaned;
 //BA.debugLineNum = 217;BA.debugLine="If snippet.Length > 2000 Then snippet = snippet";
if (_snippet.length()>2000) { 
_snippet = _snippet.substring((int) (0),(int) (2000));};
 //BA.debugLineNum = 218;BA.debugLine="Log(\"FINAL_PARSE_FAILED_SNIPPET: \" & snippet)";
anywheresoftware.b4a.keywords.Common.LogImpl("01900662","FINAL_PARSE_FAILED_SNIPPET: "+_snippet,0);
 } 
       catch (Exception e100) {
			processBA.setLastException(e100); //BA.debugLineNum = 220;BA.debugLine="Log(\"Could not produce FINAL_PARSE_FAILED_SNIPP";
anywheresoftware.b4a.keywords.Common.LogImpl("01900664","Could not produce FINAL_PARSE_FAILED_SNIPPET",0);
 };
 //BA.debugLineNum = 222;BA.debugLine="ToastMessageShow(\"No se pudo parsear la respuest";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se pudo parsear la respuesta (ver Logcat)."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 223;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 224;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 228;BA.debugLine="Dim list As List";
_list = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 229;BA.debugLine="list.Initialize";
_list.Initialize();
 //BA.debugLineNum = 231;BA.debugLine="Try";
try { //BA.debugLineNum = 232;BA.debugLine="If root Is List Then";
if (_root instanceof java.util.List) { 
 //BA.debugLineNum = 233;BA.debugLine="list = root";
_list = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root));
 }else if(_root instanceof java.util.Map) { 
 //BA.debugLineNum = 235;BA.debugLine="Dim mp As Map = root";
_mp = new anywheresoftware.b4a.objects.collections.Map();
_mp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_root));
 //BA.debugLineNum = 236;BA.debugLine="If mp.ContainsKey(\"data\") And mp.Get(\"data\") Is";
if (_mp.ContainsKey((Object)("data")) && _mp.Get((Object)("data")) instanceof java.util.List) { 
 //BA.debugLineNum = 237;BA.debugLine="list = mp.Get(\"data\")";
_list = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_mp.Get((Object)("data"))));
 }else if(_mp.ContainsKey((Object)("reportes")) && _mp.Get((Object)("reportes")) instanceof java.util.List) { 
 //BA.debugLineNum = 239;BA.debugLine="list = mp.Get(\"reportes\")";
_list = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_mp.Get((Object)("reportes"))));
 }else {
 //BA.debugLineNum = 242;BA.debugLine="For Each k As String In mp.Keys";
{
final anywheresoftware.b4a.BA.IterableList group118 = _mp.Keys();
final int groupLen118 = group118.getSize()
;int index118 = 0;
;
for (; index118 < groupLen118;index118++){
_k = BA.ObjectToString(group118.Get(index118));
 //BA.debugLineNum = 243;BA.debugLine="Dim v As Object = mp.Get(k)";
_v = _mp.Get((Object)(_k));
 //BA.debugLineNum = 244;BA.debugLine="If v Is List Then";
if (_v instanceof java.util.List) { 
 //BA.debugLineNum = 245;BA.debugLine="list = v";
_list = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_v));
 //BA.debugLineNum = 246;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 251;BA.debugLine="If (list.IsInitialized = False Or list.Size =";
if ((_list.IsInitialized()==anywheresoftware.b4a.keywords.Common.False || _list.getSize()==0)) { 
 //BA.debugLineNum = 252;BA.debugLine="If mp.ContainsKey(\"area\") Or mp.ContainsKey(\"";
if (_mp.ContainsKey((Object)("area")) || _mp.ContainsKey((Object)("car_reports")) || _mp.ContainsKey((Object)("JSON_Reporte"))) { 
 //BA.debugLineNum = 253;BA.debugLine="list.Initialize";
_list.Initialize();
 //BA.debugLineNum = 254;BA.debugLine="list.Add(mp)";
_list.Add((Object)(_mp.getObject()));
 };
 };
 };
 };
 } 
       catch (Exception e134) {
			processBA.setLastException(e134); //BA.debugLineNum = 260;BA.debugLine="Log(\"JobDone building list error: \" & LastExcept";
anywheresoftware.b4a.keywords.Common.LogImpl("01900704","JobDone building list error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 261;BA.debugLine="ToastMessageShow(\"Error procesando estructura de";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error procesando estructura de respuesta."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 262;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 263;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 267;BA.debugLine="reportsMap.Clear";
mostCurrent._reportsmap.Clear();
 //BA.debugLineNum = 268;BA.debugLine="lv.Clear";
mostCurrent._lv.Clear();
 //BA.debugLineNum = 270;BA.debugLine="For i = 0 To list.Size - 1";
{
final int step141 = 1;
final int limit141 = (int) (_list.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit141 ;_i = _i + step141 ) {
 //BA.debugLineNum = 271;BA.debugLine="Dim rawItem As Object = list.Get(i)";
_rawitem = _list.Get(_i);
 //BA.debugLineNum = 272;BA.debugLine="Dim reportContent As Map";
_reportcontent = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 273;BA.debugLine="reportContent = Null";
_reportcontent = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 276;BA.debugLine="Dim dateStr As String = \"\"";
_datestr = "";
 //BA.debugLineNum = 277;BA.debugLine="If rawItem Is Map Then";
if (_rawitem instanceof java.util.Map) { 
 //BA.debugLineNum = 278;BA.debugLine="Dim rm As Map = rawItem";
_rm = new anywheresoftware.b4a.objects.collections.Map();
_rm = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_rawitem));
 //BA.debugLineNum = 279;BA.debugLine="If rm.ContainsKey(\"FechaRegistro_Reporte\") Then";
if (_rm.ContainsKey((Object)("FechaRegistro_Reporte"))) { 
_datestr = BA.ObjectToString(_rm.Get((Object)("FechaRegistro_Reporte")));};
 //BA.debugLineNum = 280;BA.debugLine="If dateStr = \"\" And rm.ContainsKey(\"created_at\"";
if ((_datestr).equals("") && _rm.ContainsKey((Object)("created_at"))) { 
_datestr = BA.ObjectToString(_rm.Get((Object)("created_at")));};
 };
 //BA.debugLineNum = 283;BA.debugLine="If rawItem Is Map Then";
if (_rawitem instanceof java.util.Map) { 
 //BA.debugLineNum = 284;BA.debugLine="Dim candidate As Map = rawItem";
_candidate = new anywheresoftware.b4a.objects.collections.Map();
_candidate = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_rawitem));
 //BA.debugLineNum = 287;BA.debugLine="If candidate.ContainsKey(\"JSON_Reporte_parsed\")";
if (_candidate.ContainsKey((Object)("JSON_Reporte_parsed"))) { 
 //BA.debugLineNum = 288;BA.debugLine="Dim parsedObj As Object = candidate.Get(\"JSON_";
_parsedobj = _candidate.Get((Object)("JSON_Reporte_parsed"));
 //BA.debugLineNum = 289;BA.debugLine="If parsedObj Is Map Then";
if (_parsedobj instanceof java.util.Map) { 
 //BA.debugLineNum = 290;BA.debugLine="reportContent = parsedObj";
_reportcontent = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_parsedobj));
 }else if(_parsedobj instanceof java.util.List) { 
 //BA.debugLineNum = 292;BA.debugLine="Dim pl As List = parsedObj";
_pl = new anywheresoftware.b4a.objects.collections.List();
_pl = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_parsedobj));
 //BA.debugLineNum = 293;BA.debugLine="If pl.Size > 0 And pl.Get(0) Is Map Then repo";
if (_pl.getSize()>0 && _pl.Get((int) (0)) instanceof java.util.Map) { 
_reportcontent = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_pl.Get((int) (0))));};
 };
 };
 //BA.debugLineNum = 298;BA.debugLine="If reportContent = Null And candidate.ContainsK";
if (_reportcontent== null && _candidate.ContainsKey((Object)("JSON_Reporte"))) { 
 //BA.debugLineNum = 299;BA.debugLine="Try";
try { //BA.debugLineNum = 300;BA.debugLine="Dim sjson As String = candidate.Get(\"JSON_Rep";
_sjson = BA.ObjectToString(_candidate.Get((Object)("JSON_Reporte")));
 //BA.debugLineNum = 301;BA.debugLine="If sjson <> \"\" Then";
if ((_sjson).equals("") == false) { 
 //BA.debugLineNum = 302;BA.debugLine="Dim p As JSONParser";
_p = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 303;BA.debugLine="p.Initialize(sjson)";
_p.Initialize(_sjson);
 //BA.debugLineNum = 304;BA.debugLine="Dim maybe As Object = p.NextValue";
_maybe = _p.NextValue();
 //BA.debugLineNum = 305;BA.debugLine="If maybe Is Map Then";
if (_maybe instanceof java.util.Map) { 
 //BA.debugLineNum = 306;BA.debugLine="reportContent = maybe";
_reportcontent = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_maybe));
 }else if(_maybe instanceof java.util.List) { 
 //BA.debugLineNum = 308;BA.debugLine="Dim ltmp As List = maybe";
_ltmp = new anywheresoftware.b4a.objects.collections.List();
_ltmp = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_maybe));
 //BA.debugLineNum = 309;BA.debugLine="If ltmp.Size > 0 And ltmp.Get(0) Is Map The";
if (_ltmp.getSize()>0 && _ltmp.Get((int) (0)) instanceof java.util.Map) { 
_reportcontent = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_ltmp.Get((int) (0))));};
 };
 };
 } 
       catch (Exception e177) {
			processBA.setLastException(e177); //BA.debugLineNum = 313;BA.debugLine="Log(\"Error parsing JSON_Reporte: \" & LastExce";
anywheresoftware.b4a.keywords.Common.LogImpl("01900757","Error parsing JSON_Reporte: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 318;BA.debugLine="If reportContent = Null Then";
if (_reportcontent== null) { 
 //BA.debugLineNum = 319;BA.debugLine="If candidate.ContainsKey(\"area\") Or candidate.";
if (_candidate.ContainsKey((Object)("area")) || _candidate.ContainsKey((Object)("car_reports"))) { 
 //BA.debugLineNum = 320;BA.debugLine="reportContent = candidate";
_reportcontent = _candidate;
 };
 };
 }else {
 //BA.debugLineNum = 325;BA.debugLine="Try";
try { //BA.debugLineNum = 326;BA.debugLine="Dim s2 As String = rawItem";
_s2 = BA.ObjectToString(_rawitem);
 //BA.debugLineNum = 327;BA.debugLine="Dim p3 As JSONParser";
_p3 = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 328;BA.debugLine="p3.Initialize(s2)";
_p3.Initialize(_s2);
 //BA.debugLineNum = 329;BA.debugLine="Dim maybe2 As Object = p3.NextValue";
_maybe2 = _p3.NextValue();
 //BA.debugLineNum = 330;BA.debugLine="If maybe2 Is Map Then";
if (_maybe2 instanceof java.util.Map) { 
 //BA.debugLineNum = 331;BA.debugLine="reportContent = maybe2";
_reportcontent = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_maybe2));
 };
 } 
       catch (Exception e195) {
			processBA.setLastException(e195); //BA.debugLineNum = 334;BA.debugLine="Log(\"Ignored non-map rawItem parse error: \" &";
anywheresoftware.b4a.keywords.Common.LogImpl("01900778","Ignored non-map rawItem parse error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 338;BA.debugLine="If reportContent = Null Then";
if (_reportcontent== null) { 
 //BA.debugLineNum = 339;BA.debugLine="reportContent.Initialize";
_reportcontent.Initialize();
 //BA.debugLineNum = 340;BA.debugLine="reportContent.Put(\"raw\", rawItem)";
_reportcontent.Put((Object)("raw"),_rawitem);
 };
 //BA.debugLineNum = 344;BA.debugLine="Dim title As String = \"\"";
_title = "";
 //BA.debugLineNum = 345;BA.debugLine="If reportContent.ContainsKey(\"area\") Then";
if (_reportcontent.ContainsKey((Object)("area"))) { 
 //BA.debugLineNum = 346;BA.debugLine="Try";
try { //BA.debugLineNum = 347;BA.debugLine="Dim areaMap As Map = reportContent.Get(\"area\")";
_areamap = new anywheresoftware.b4a.objects.collections.Map();
_areamap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_reportcontent.Get((Object)("area"))));
 //BA.debugLineNum = 348;BA.debugLine="If areaMap.ContainsKey(\"area_name\") Then title";
if (_areamap.ContainsKey((Object)("area_name"))) { 
_title = BA.ObjectToString(_areamap.Get((Object)("area_name")));};
 //BA.debugLineNum = 349;BA.debugLine="If title = \"\" And areaMap.ContainsKey(\"areaNam";
if ((_title).equals("") && _areamap.ContainsKey((Object)("areaName"))) { 
_title = BA.ObjectToString(_areamap.Get((Object)("areaName")));};
 } 
       catch (Exception e209) {
			processBA.setLastException(e209); //BA.debugLineNum = 351;BA.debugLine="Log(\"Error leyendo area_name: \" & LastExceptio";
anywheresoftware.b4a.keywords.Common.LogImpl("01900795","Error leyendo area_name: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 355;BA.debugLine="If title = \"\" Then";
if ((_title).equals("")) { 
 //BA.debugLineNum = 356;BA.debugLine="If reportContent.ContainsKey(\"Id_Reporte\") Then";
if (_reportcontent.ContainsKey((Object)("Id_Reporte"))) { 
 //BA.debugLineNum = 357;BA.debugLine="title = \"Reporte \" & reportContent.Get(\"Id_Rep";
_title = "Reporte "+BA.ObjectToString(_reportcontent.Get((Object)("Id_Reporte")));
 }else {
 //BA.debugLineNum = 359;BA.debugLine="If reportContent.ContainsKey(\"id\") Then";
if (_reportcontent.ContainsKey((Object)("id"))) { 
 //BA.debugLineNum = 360;BA.debugLine="title = \"Reporte \" & reportContent.Get(\"id\")";
_title = "Reporte "+BA.ObjectToString(_reportcontent.Get((Object)("id")));
 }else {
 //BA.debugLineNum = 362;BA.debugLine="title = \"Reporte \" & (i + 1)";
_title = "Reporte "+BA.NumberToString((_i+1));
 };
 };
 };
 //BA.debugLineNum = 368;BA.debugLine="If dateStr <> \"\" Then";
if ((_datestr).equals("") == false) { 
 //BA.debugLineNum = 369;BA.debugLine="title = dateStr & \" - \" & title";
_title = _datestr+" - "+_title;
 };
 //BA.debugLineNum = 372;BA.debugLine="lv.AddSingleLine2(title, i)";
mostCurrent._lv.AddSingleLine2(BA.ObjectToCharSequence(_title),(Object)(_i));
 //BA.debugLineNum = 375;BA.debugLine="Dim stored As Map";
_stored = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 376;BA.debugLine="stored.Initialize";
_stored.Initialize();
 //BA.debugLineNum = 377;BA.debugLine="stored.Put(\"parsed\", reportContent) ' Map (puede";
_stored.Put((Object)("parsed"),(Object)(_reportcontent.getObject()));
 //BA.debugLineNum = 378;BA.debugLine="If rawItem Is Map Then";
if (_rawitem instanceof java.util.Map) { 
 //BA.debugLineNum = 379;BA.debugLine="stored.Put(\"raw\", rawItem) ' fila original devu";
_stored.Put((Object)("raw"),_rawitem);
 }else {
 //BA.debugLineNum = 381;BA.debugLine="stored.Put(\"raw\", Null)";
_stored.Put((Object)("raw"),anywheresoftware.b4a.keywords.Common.Null);
 };
 //BA.debugLineNum = 383;BA.debugLine="reportsMap.Put(i, stored)";
mostCurrent._reportsmap.Put((Object)(_i),(Object)(_stored.getObject()));
 }
};
 //BA.debugLineNum = 387;BA.debugLine="If lv.Size = 0 Then ToastMessageShow(\"No se encon";
if (mostCurrent._lv.getSize()==0) { 
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se encontraron reportes para el área."),anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 388;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 389;BA.debugLine="End Sub";
return "";
}
public static String  _lvreports_itemclick(int _position,Object _value) throws Exception{
int _idx = 0;
anywheresoftware.b4a.objects.collections.Map _stored = null;
anywheresoftware.b4a.objects.collections.Map _parsed = null;
anywheresoftware.b4a.objects.collections.Map _raw = null;
Object _tmpparsed = null;
Object _tmpraw = null;
anywheresoftware.b4a.objects.collections.Map _out = null;
anywheresoftware.b4a.objects.collections.Map _jrmap = null;
String _s = "";
anywheresoftware.b4a.objects.collections.JSONParser _p = null;
Object _maybe = null;
anywheresoftware.b4a.objects.collections.Map _m = null;
String _k = "";
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _gen = null;
String _jsonstr = "";
 //BA.debugLineNum = 459;BA.debugLine="Sub lvReports_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 460;BA.debugLine="Dim idx As Int = Value";
_idx = (int)(BA.ObjectToNumber(_value));
 //BA.debugLineNum = 461;BA.debugLine="Log(\"lvReports_ItemClick fired. idx=\" & idx)";
anywheresoftware.b4a.keywords.Common.LogImpl("02031618","lvReports_ItemClick fired. idx="+BA.NumberToString(_idx),0);
 //BA.debugLineNum = 462;BA.debugLine="ToastMessageShow(\"Click: \" & idx, False) ' confir";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Click: "+BA.NumberToString(_idx)),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 464;BA.debugLine="If reportsMap.IsInitialized = False Then";
if (mostCurrent._reportsmap.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 465;BA.debugLine="Log(\"lvReports_ItemClick: reportsMap no iniciali";
anywheresoftware.b4a.keywords.Common.LogImpl("02031622","lvReports_ItemClick: reportsMap no inicializado",0);
 //BA.debugLineNum = 466;BA.debugLine="MsgboxAsync(\"Mapa de reportes no inicializado\",";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Mapa de reportes no inicializado"),BA.ObjectToCharSequence("Error"),processBA);
 //BA.debugLineNum = 467;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 470;BA.debugLine="If reportsMap.ContainsKey(idx) = False Then";
if (mostCurrent._reportsmap.ContainsKey((Object)(_idx))==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 471;BA.debugLine="Log(\"lvReports_ItemClick: reportsMap no contiene";
anywheresoftware.b4a.keywords.Common.LogImpl("02031628","lvReports_ItemClick: reportsMap no contiene idx="+BA.NumberToString(_idx),0);
 //BA.debugLineNum = 472;BA.debugLine="MsgboxAsync(\"Reporte no encontrado\", \"Error\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Reporte no encontrado"),BA.ObjectToCharSequence("Error"),processBA);
 //BA.debugLineNum = 473;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 476;BA.debugLine="Dim stored As Map";
_stored = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 477;BA.debugLine="stored = Null";
_stored = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 478;BA.debugLine="Try";
try { //BA.debugLineNum = 479;BA.debugLine="stored = reportsMap.Get(idx)";
_stored = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._reportsmap.Get((Object)(_idx))));
 } 
       catch (Exception e19) {
			processBA.setLastException(e19); //BA.debugLineNum = 481;BA.debugLine="Log(\"lvReports_ItemClick: error al obtener store";
anywheresoftware.b4a.keywords.Common.LogImpl("02031638","lvReports_ItemClick: error al obtener stored: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 482;BA.debugLine="MsgboxAsync(\"Reporte inválido\", \"Error\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Reporte inválido"),BA.ObjectToCharSequence("Error"),processBA);
 //BA.debugLineNum = 483;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 486;BA.debugLine="If stored = Null Then";
if (_stored== null) { 
 //BA.debugLineNum = 487;BA.debugLine="Log(\"lvReports_ItemClick: stored es Null\")";
anywheresoftware.b4a.keywords.Common.LogImpl("02031644","lvReports_ItemClick: stored es Null",0);
 //BA.debugLineNum = 488;BA.debugLine="MsgboxAsync(\"Reporte inválido\", \"Error\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Reporte inválido"),BA.ObjectToCharSequence("Error"),processBA);
 //BA.debugLineNum = 489;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 493;BA.debugLine="Dim parsed As Map = Null";
_parsed = new anywheresoftware.b4a.objects.collections.Map();
_parsed = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 494;BA.debugLine="Dim raw As Map = Null";
_raw = new anywheresoftware.b4a.objects.collections.Map();
_raw = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 495;BA.debugLine="Try";
try { //BA.debugLineNum = 496;BA.debugLine="If stored.ContainsKey(\"parsed\") Then";
if (_stored.ContainsKey((Object)("parsed"))) { 
 //BA.debugLineNum = 497;BA.debugLine="Dim tmpParsed As Object = stored.Get(\"parsed\")";
_tmpparsed = _stored.Get((Object)("parsed"));
 //BA.debugLineNum = 498;BA.debugLine="If tmpParsed Is Map Then parsed = tmpParsed";
if (_tmpparsed instanceof java.util.Map) { 
_parsed = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_tmpparsed));};
 };
 } 
       catch (Exception e36) {
			processBA.setLastException(e36); //BA.debugLineNum = 501;BA.debugLine="Log(\"lvReports_ItemClick: error leyendo parsed:";
anywheresoftware.b4a.keywords.Common.LogImpl("02031658","lvReports_ItemClick: error leyendo parsed: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 504;BA.debugLine="Try";
try { //BA.debugLineNum = 505;BA.debugLine="If stored.ContainsKey(\"raw\") Then";
if (_stored.ContainsKey((Object)("raw"))) { 
 //BA.debugLineNum = 506;BA.debugLine="Dim tmpRaw As Object = stored.Get(\"raw\")";
_tmpraw = _stored.Get((Object)("raw"));
 //BA.debugLineNum = 507;BA.debugLine="If tmpRaw Is Map Then raw = tmpRaw";
if (_tmpraw instanceof java.util.Map) { 
_raw = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_tmpraw));};
 };
 } 
       catch (Exception e44) {
			processBA.setLastException(e44); //BA.debugLineNum = 510;BA.debugLine="Log(\"lvReports_ItemClick: error leyendo raw: \" &";
anywheresoftware.b4a.keywords.Common.LogImpl("02031667","lvReports_ItemClick: error leyendo raw: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 514;BA.debugLine="If parsed = Null And raw Is Map Then parsed = raw";
if (_parsed== null && _raw.getObjectOrNull() instanceof java.util.Map) { 
_parsed = _raw;};
 //BA.debugLineNum = 516;BA.debugLine="If parsed = Null Then";
if (_parsed== null) { 
 //BA.debugLineNum = 517;BA.debugLine="Log(\"lvReports_ItemClick: no hay parsed ni raw ú";
anywheresoftware.b4a.keywords.Common.LogImpl("02031674","lvReports_ItemClick: no hay parsed ni raw útiles",0);
 //BA.debugLineNum = 518;BA.debugLine="MsgboxAsync(\"No hay datos válidos para este repo";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("No hay datos válidos para este reporte."),BA.ObjectToCharSequence("Error"),processBA);
 //BA.debugLineNum = 519;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 523;BA.debugLine="Dim out As Map";
_out = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 524;BA.debugLine="out.Initialize";
_out.Initialize();
 //BA.debugLineNum = 526;BA.debugLine="Try";
try { //BA.debugLineNum = 527;BA.debugLine="If parsed.ContainsKey(\"area\") Then out.Put(\"area";
if (_parsed.ContainsKey((Object)("area"))) { 
_out.Put((Object)("area"),_parsed.Get((Object)("area")));};
 } 
       catch (Exception e57) {
			processBA.setLastException(e57); //BA.debugLineNum = 529;BA.debugLine="Log(\"lvReports_ItemClick: error copiando parsed.";
anywheresoftware.b4a.keywords.Common.LogImpl("02031686","lvReports_ItemClick: error copiando parsed.area: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 531;BA.debugLine="Try";
try { //BA.debugLineNum = 532;BA.debugLine="If parsed.ContainsKey(\"car_reports\") Then out.Pu";
if (_parsed.ContainsKey((Object)("car_reports"))) { 
_out.Put((Object)("car_reports"),_parsed.Get((Object)("car_reports")));};
 } 
       catch (Exception e62) {
			processBA.setLastException(e62); //BA.debugLineNum = 534;BA.debugLine="Log(\"lvReports_ItemClick: error copiando parsed.";
anywheresoftware.b4a.keywords.Common.LogImpl("02031691","lvReports_ItemClick: error copiando parsed.car_reports: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 537;BA.debugLine="If (out.ContainsKey(\"area\") = False Or out.Contai";
if ((_out.ContainsKey((Object)("area"))==anywheresoftware.b4a.keywords.Common.False || _out.ContainsKey((Object)("car_reports"))==anywheresoftware.b4a.keywords.Common.False)) { 
 //BA.debugLineNum = 538;BA.debugLine="If raw Is Map Then";
if (_raw.getObjectOrNull() instanceof java.util.Map) { 
 //BA.debugLineNum = 540;BA.debugLine="If raw.ContainsKey(\"JSON_Reporte_parsed\") Then";
if (_raw.ContainsKey((Object)("JSON_Reporte_parsed"))) { 
 //BA.debugLineNum = 541;BA.debugLine="Try";
try { //BA.debugLineNum = 542;BA.debugLine="Dim jrMap As Map = raw.Get(\"JSON_Reporte_pars";
_jrmap = new anywheresoftware.b4a.objects.collections.Map();
_jrmap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_raw.Get((Object)("JSON_Reporte_parsed"))));
 //BA.debugLineNum = 543;BA.debugLine="If jrMap <> Null Then";
if (_jrmap!= null) { 
 //BA.debugLineNum = 544;BA.debugLine="If out.ContainsKey(\"area\") = False And jrMap";
if (_out.ContainsKey((Object)("area"))==anywheresoftware.b4a.keywords.Common.False && _jrmap.ContainsKey((Object)("area"))) { 
 //BA.debugLineNum = 545;BA.debugLine="out.Put(\"area\", jrMap.Get(\"area\"))";
_out.Put((Object)("area"),_jrmap.Get((Object)("area")));
 };
 //BA.debugLineNum = 547;BA.debugLine="If out.ContainsKey(\"car_reports\") = False An";
if (_out.ContainsKey((Object)("car_reports"))==anywheresoftware.b4a.keywords.Common.False && _jrmap.ContainsKey((Object)("car_reports"))) { 
 //BA.debugLineNum = 548;BA.debugLine="out.Put(\"car_reports\", jrMap.Get(\"car_repor";
_out.Put((Object)("car_reports"),_jrmap.Get((Object)("car_reports")));
 };
 };
 } 
       catch (Exception e78) {
			processBA.setLastException(e78); //BA.debugLineNum = 552;BA.debugLine="Log(\"lvReports_ItemClick: raw.JSON_Reporte_pa";
anywheresoftware.b4a.keywords.Common.LogImpl("02031709","lvReports_ItemClick: raw.JSON_Reporte_parsed no es Map o error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 557;BA.debugLine="If (out.ContainsKey(\"area\") = False Or out.Cont";
if ((_out.ContainsKey((Object)("area"))==anywheresoftware.b4a.keywords.Common.False || _out.ContainsKey((Object)("car_reports"))==anywheresoftware.b4a.keywords.Common.False) && _raw.ContainsKey((Object)("JSON_Reporte"))) { 
 //BA.debugLineNum = 558;BA.debugLine="Try";
try { //BA.debugLineNum = 559;BA.debugLine="Dim s As String = raw.Get(\"JSON_Reporte\")";
_s = BA.ObjectToString(_raw.Get((Object)("JSON_Reporte")));
 //BA.debugLineNum = 560;BA.debugLine="If s <> \"\" Then";
if ((_s).equals("") == false) { 
 //BA.debugLineNum = 561;BA.debugLine="Dim p As JSONParser";
_p = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 562;BA.debugLine="p.Initialize(s)";
_p.Initialize(_s);
 //BA.debugLineNum = 563;BA.debugLine="Dim maybe As Object = p.NextValue";
_maybe = _p.NextValue();
 //BA.debugLineNum = 564;BA.debugLine="If maybe Is Map Then";
if (_maybe instanceof java.util.Map) { 
 //BA.debugLineNum = 565;BA.debugLine="Dim m As Map = maybe";
_m = new anywheresoftware.b4a.objects.collections.Map();
_m = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_maybe));
 //BA.debugLineNum = 566;BA.debugLine="If out.ContainsKey(\"area\") = False And m.Co";
if (_out.ContainsKey((Object)("area"))==anywheresoftware.b4a.keywords.Common.False && _m.ContainsKey((Object)("area"))) { 
_out.Put((Object)("area"),_m.Get((Object)("area")));};
 //BA.debugLineNum = 567;BA.debugLine="If out.ContainsKey(\"car_reports\") = False A";
if (_out.ContainsKey((Object)("car_reports"))==anywheresoftware.b4a.keywords.Common.False && _m.ContainsKey((Object)("car_reports"))) { 
_out.Put((Object)("car_reports"),_m.Get((Object)("car_reports")));};
 };
 };
 } 
       catch (Exception e95) {
			processBA.setLastException(e95); //BA.debugLineNum = 571;BA.debugLine="Log(\"lvReports_ItemClick: error parseando JSO";
anywheresoftware.b4a.keywords.Common.LogImpl("02031728","lvReports_ItemClick: error parseando JSON_Reporte: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 };
 };
 //BA.debugLineNum = 578;BA.debugLine="If raw Is Map Then";
if (_raw.getObjectOrNull() instanceof java.util.Map) { 
 //BA.debugLineNum = 579;BA.debugLine="Try";
try { //BA.debugLineNum = 580;BA.debugLine="For Each k As String In raw.Keys";
{
final anywheresoftware.b4a.BA.IterableList group102 = _raw.Keys();
final int groupLen102 = group102.getSize()
;int index102 = 0;
;
for (; index102 < groupLen102;index102++){
_k = BA.ObjectToString(group102.Get(index102));
 //BA.debugLineNum = 581;BA.debugLine="Try";
try { //BA.debugLineNum = 582;BA.debugLine="If out.ContainsKey(k) = False Then out.Put(k,";
if (_out.ContainsKey((Object)(_k))==anywheresoftware.b4a.keywords.Common.False) { 
_out.Put((Object)(_k),_raw.Get((Object)(_k)));};
 } 
       catch (Exception e106) {
			processBA.setLastException(e106); //BA.debugLineNum = 584;BA.debugLine="Log(\"lvReports_ItemClick: no pudo copiar key \"";
anywheresoftware.b4a.keywords.Common.LogImpl("02031741","lvReports_ItemClick: no pudo copiar key "+_k+": "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 }
};
 } 
       catch (Exception e110) {
			processBA.setLastException(e110); //BA.debugLineNum = 588;BA.debugLine="Log(\"lvReports_ItemClick: error iterando keys ra";
anywheresoftware.b4a.keywords.Common.LogImpl("02031745","lvReports_ItemClick: error iterando keys raw: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 594;BA.debugLine="If out.ContainsKey(\"Id_Reporte\") = False Then";
if (_out.ContainsKey((Object)("Id_Reporte"))==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 595;BA.debugLine="If raw Is Map And raw.ContainsKey(\"Id_Reporte\")";
if (_raw.getObjectOrNull() instanceof java.util.Map && _raw.ContainsKey((Object)("Id_Reporte"))) { 
_out.Put((Object)("Id_Reporte"),_raw.Get((Object)("Id_Reporte")));};
 };
 //BA.debugLineNum = 599;BA.debugLine="Try";
try { //BA.debugLineNum = 600;BA.debugLine="Dim gen As JSONGenerator";
_gen = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 601;BA.debugLine="gen.Initialize(out)";
_gen.Initialize(_out);
 //BA.debugLineNum = 602;BA.debugLine="Dim jsonStr As String = gen.ToString";
_jsonstr = _gen.ToString();
 //BA.debugLineNum = 603;BA.debugLine="File.WriteString(File.DirInternal, \"current_repo";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json",_jsonstr);
 //BA.debugLineNum = 604;BA.debugLine="Log(\"lvReports_ItemClick: wrote current_report.j";
anywheresoftware.b4a.keywords.Common.LogImpl("02031761","lvReports_ItemClick: wrote current_report.json length="+BA.NumberToString(_jsonstr.length()),0);
 //BA.debugLineNum = 605;BA.debugLine="Log(\"lvReports_ItemClick snippet: \" & jsonStr.Su";
anywheresoftware.b4a.keywords.Common.LogImpl("02031762","lvReports_ItemClick snippet: "+_jsonstr.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(800,_jsonstr.length()))),0);
 } 
       catch (Exception e124) {
			processBA.setLastException(e124); //BA.debugLineNum = 607;BA.debugLine="Log(\"lvReports_ItemClick: Error writing current_";
anywheresoftware.b4a.keywords.Common.LogImpl("02031764","lvReports_ItemClick: Error writing current_report.json: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 608;BA.debugLine="MsgboxAsync(\"No se pudo preparar el detalle del";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("No se pudo preparar el detalle del reporte."),BA.ObjectToCharSequence("Error"),processBA);
 //BA.debugLineNum = 609;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 612;BA.debugLine="ReportDetail.AllowEdit = False";
mostCurrent._reportdetail._allowedit /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 613;BA.debugLine="StartActivity(ReportDetail)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._reportdetail.getObject()));
 //BA.debugLineNum = 614;BA.debugLine="End Sub";
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
