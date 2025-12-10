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

public class reportdetail extends Activity implements B4AActivity{
	public static reportdetail mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.reportdetail");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (reportdetail).");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.reportdetail");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.reportdetail", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (reportdetail) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (reportdetail) Resume **");
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
		return reportdetail.class;
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
            BA.LogInfo("** Activity (reportdetail) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (reportdetail) Pause event (activity is not paused). **");
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
            reportdetail mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (reportdetail) Resume **");
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
public static String _reportjson = "";
public anywheresoftware.b4a.objects.ScrollViewWrapper _sv = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.menuprincipal _menuprincipal = null;
public b4a.example.reportsbyarea _reportsbyarea = null;
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
 //BA.debugLineNum = 19;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 20;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 21;BA.debugLine="Activity.Title = \"Detalle reporte\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Detalle reporte"));
 //BA.debugLineNum = 23;BA.debugLine="sv.Initialize(100%y)";
mostCurrent._sv.Initialize(mostCurrent.activityBA,anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 24;BA.debugLine="Activity.AddView(sv, 0, 0, 100%x, 100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._sv.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 25;BA.debugLine="pnl = sv.Panel";
mostCurrent._pnl = mostCurrent._sv.getPanel();
 //BA.debugLineNum = 26;BA.debugLine="pnl.RemoveAllViews";
mostCurrent._pnl.RemoveAllViews();
 //BA.debugLineNum = 28;BA.debugLine="LoadAndRenderCurrentReport";
_loadandrendercurrentreport();
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 35;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 31;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}
public static int  _estimatelabelheight(String _text,int _textsize,int _width) throws Exception{
int _charwidth = 0;
int _charsperline = 0;
int _textlen = 0;
int _lines = 0;
int _lineheight = 0;
int _result = 0;
 //BA.debugLineNum = 385;BA.debugLine="Sub EstimateLabelHeight(text As String, textSize A";
 //BA.debugLineNum = 386;BA.debugLine="If text = \"\" Then Return 30dip";
if ((_text).equals("")) { 
if (true) return anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30));};
 //BA.debugLineNum = 387;BA.debugLine="If width <= 0 Then width = 100dip";
if (_width<=0) { 
_width = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100));};
 //BA.debugLineNum = 388;BA.debugLine="Dim charWidth As Int = (textSize * 6) / 10";
_charwidth = (int) ((_textsize*6)/(double)10);
 //BA.debugLineNum = 389;BA.debugLine="If charWidth < 4 Then charWidth = 4";
if (_charwidth<4) { 
_charwidth = (int) (4);};
 //BA.debugLineNum = 390;BA.debugLine="Dim charsPerLine As Int = width / charWidth";
_charsperline = (int) (_width/(double)_charwidth);
 //BA.debugLineNum = 391;BA.debugLine="If charsPerLine < 1 Then charsPerLine = 1";
if (_charsperline<1) { 
_charsperline = (int) (1);};
 //BA.debugLineNum = 392;BA.debugLine="Dim textLen As Int = text.Length";
_textlen = _text.length();
 //BA.debugLineNum = 393;BA.debugLine="Dim lines As Int = textLen / charsPerLine";
_lines = (int) (_textlen/(double)_charsperline);
 //BA.debugLineNum = 394;BA.debugLine="If (textLen Mod charsPerLine) <> 0 Then lines = l";
if ((_textlen%_charsperline)!=0) { 
_lines = (int) (_lines+1);};
 //BA.debugLineNum = 395;BA.debugLine="Dim lineHeight As Int = (textSize * 16) / 10";
_lineheight = (int) ((_textsize*16)/(double)10);
 //BA.debugLineNum = 396;BA.debugLine="If lineHeight < 14 Then lineHeight = 14";
if (_lineheight<14) { 
_lineheight = (int) (14);};
 //BA.debugLineNum = 397;BA.debugLine="Dim result As Int = lines * lineHeight + 6dip";
_result = (int) (_lines*_lineheight+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 //BA.debugLineNum = 398;BA.debugLine="Return result";
if (true) return _result;
 //BA.debugLineNum = 399;BA.debugLine="End Sub";
return 0;
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
 //BA.debugLineNum = 187;BA.debugLine="Sub ExtractFirstJson(s As String) As String";
 //BA.debugLineNum = 188;BA.debugLine="If s = Null Then Return \"\"";
if (_s== null) { 
if (true) return "";};
 //BA.debugLineNum = 189;BA.debugLine="s = s.Trim";
_s = _s.trim();
 //BA.debugLineNum = 190;BA.debugLine="Dim len As Int = s.Length";
_len = _s.length();
 //BA.debugLineNum = 191;BA.debugLine="If len = 0 Then Return \"\"";
if (_len==0) { 
if (true) return "";};
 //BA.debugLineNum = 193;BA.debugLine="Dim firstObj As Int = s.IndexOf(\"{\")";
_firstobj = _s.indexOf("{");
 //BA.debugLineNum = 194;BA.debugLine="Dim firstArr As Int = s.IndexOf(\"[\")";
_firstarr = _s.indexOf("[");
 //BA.debugLineNum = 195;BA.debugLine="Dim start As Int";
_start = 0;
 //BA.debugLineNum = 196;BA.debugLine="If firstObj = -1 And firstArr = -1 Then Return \"\"";
if (_firstobj==-1 && _firstarr==-1) { 
if (true) return "";};
 //BA.debugLineNum = 197;BA.debugLine="If firstObj = -1 Then";
if (_firstobj==-1) { 
 //BA.debugLineNum = 198;BA.debugLine="start = firstArr";
_start = _firstarr;
 }else if(_firstarr==-1) { 
 //BA.debugLineNum = 200;BA.debugLine="start = firstObj";
_start = _firstobj;
 }else {
 //BA.debugLineNum = 202;BA.debugLine="If firstObj < firstArr Then";
if (_firstobj<_firstarr) { 
 //BA.debugLineNum = 203;BA.debugLine="start = firstObj";
_start = _firstobj;
 }else {
 //BA.debugLineNum = 205;BA.debugLine="start = firstArr";
_start = _firstarr;
 };
 };
 //BA.debugLineNum = 209;BA.debugLine="Dim depth As Int = 0";
_depth = (int) (0);
 //BA.debugLineNum = 210;BA.debugLine="Dim inString As Boolean = False";
_instring = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 211;BA.debugLine="Dim escaped As Boolean = False";
_escaped = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 213;BA.debugLine="For i = start To len - 1";
{
final int step23 = 1;
final int limit23 = (int) (_len-1);
_i = _start ;
for (;_i <= limit23 ;_i = _i + step23 ) {
 //BA.debugLineNum = 214;BA.debugLine="Dim ch As String = s.SubString2(i, i + 1)";
_ch = _s.substring(_i,(int) (_i+1));
 //BA.debugLineNum = 215;BA.debugLine="If escaped Then";
if (_escaped) { 
 //BA.debugLineNum = 216;BA.debugLine="escaped = False";
_escaped = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 217;BA.debugLine="Continue";
if (true) continue;
 };
 //BA.debugLineNum = 219;BA.debugLine="If ch = \"\\\" Then";
if ((_ch).equals("\\")) { 
 //BA.debugLineNum = 220;BA.debugLine="escaped = True";
_escaped = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 221;BA.debugLine="Continue";
if (true) continue;
 };
 //BA.debugLineNum = 223;BA.debugLine="If ch = \"\"\"\" Then";
if ((_ch).equals("\"")) { 
 //BA.debugLineNum = 224;BA.debugLine="inString = Not(inString)";
_instring = anywheresoftware.b4a.keywords.Common.Not(_instring);
 //BA.debugLineNum = 225;BA.debugLine="Continue";
if (true) continue;
 };
 //BA.debugLineNum = 228;BA.debugLine="If Not(inString) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_instring)) { 
 //BA.debugLineNum = 229;BA.debugLine="If ch = \"{\" Or ch = \"[\" Then";
if ((_ch).equals("{") || (_ch).equals("[")) { 
 //BA.debugLineNum = 230;BA.debugLine="depth = depth + 1";
_depth = (int) (_depth+1);
 }else if((_ch).equals("}") || (_ch).equals("]")) { 
 //BA.debugLineNum = 232;BA.debugLine="depth = depth - 1";
_depth = (int) (_depth-1);
 //BA.debugLineNum = 233;BA.debugLine="If depth = 0 Then";
if (_depth==0) { 
 //BA.debugLineNum = 234;BA.debugLine="Return s.SubString2(start, i + 1)";
if (true) return _s.substring(_start,(int) (_i+1));
 };
 };
 };
 }
};
 //BA.debugLineNum = 240;BA.debugLine="Return \"\"";
if (true) return "";
 //BA.debugLineNum = 241;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Private sv As ScrollView";
mostCurrent._sv = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Private pnl As Panel";
mostCurrent._pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _loadandrendercurrentreport() throws Exception{
String _jsontouse = "";
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
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
anywheresoftware.b4a.objects.collections.Map _rp = null;
anywheresoftware.b4a.objects.collections.List _lst = null;
Object _maybeparsed = null;
String _sj = "";
anywheresoftware.b4a.objects.collections.JSONParser _p2 = null;
Object _inner = null;
 //BA.debugLineNum = 43;BA.debugLine="Sub LoadAndRenderCurrentReport";
 //BA.debugLineNum = 44;BA.debugLine="Dim jsonToUse As String = ReportJson";
_jsontouse = _reportjson;
 //BA.debugLineNum = 45;BA.debugLine="If jsonToUse = \"\" Then";
if ((_jsontouse).equals("")) { 
 //BA.debugLineNum = 46;BA.debugLine="Try";
try { //BA.debugLineNum = 47;BA.debugLine="If File.Exists(File.DirInternal, \"current_repor";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json")) { 
 //BA.debugLineNum = 48;BA.debugLine="jsonToUse = File.ReadString(File.DirInternal,";
_jsontouse = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json");
 };
 } 
       catch (Exception e8) {
			processBA.setLastException(e8); //BA.debugLineNum = 51;BA.debugLine="Log(\"ReportDetail: Error reading current_report";
anywheresoftware.b4a.keywords.Common.LogImpl("57274504","ReportDetail: Error reading current_report.json: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 55;BA.debugLine="If jsonToUse = Null Then jsonToUse = \"\"";
if (_jsontouse== null) { 
_jsontouse = "";};
 //BA.debugLineNum = 56;BA.debugLine="jsonToUse = jsonToUse.Trim";
_jsontouse = _jsontouse.trim();
 //BA.debugLineNum = 57;BA.debugLine="If jsonToUse = \"\" Then";
if ((_jsontouse).equals("")) { 
 //BA.debugLineNum = 58;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 59;BA.debugLine="lbl.Initialize(\"lbl_empty\")";
_lbl.Initialize(mostCurrent.activityBA,"lbl_empty");
 //BA.debugLineNum = 60;BA.debugLine="lbl.Text = \"No hay datos para mostrar.\"";
_lbl.setText(BA.ObjectToCharSequence("No hay datos para mostrar."));
 //BA.debugLineNum = 61;BA.debugLine="lbl.TextSize = 16";
_lbl.setTextSize((float) (16));
 //BA.debugLineNum = 62;BA.debugLine="pnl.AddView(lbl, 10dip, 10dip, 100%x - 20dip, Es";
mostCurrent._pnl.AddView((android.view.View)(_lbl.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),_estimatelabelheight(_lbl.getText(),(int) (_lbl.getTextSize()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)))));
 //BA.debugLineNum = 63;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 67;BA.debugLine="Try";
try { //BA.debugLineNum = 68;BA.debugLine="jsonToUse = jsonToUse.Replace(Chr(65279), \"\")";
_jsontouse = _jsontouse.replace(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (65279))),"");
 } 
       catch (Exception e24) {
			processBA.setLastException(e24); //BA.debugLineNum = 70;BA.debugLine="Log(\"ReportDetail: BOM replace error: \" & LastEx";
anywheresoftware.b4a.keywords.Common.LogImpl("57274523","ReportDetail: BOM replace error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 74;BA.debugLine="Dim extracted As String = ExtractFirstJson(jsonTo";
_extracted = _extractfirstjson(_jsontouse);
 //BA.debugLineNum = 75;BA.debugLine="If extracted = \"\" Then";
if ((_extracted).equals("")) { 
 //BA.debugLineNum = 76;BA.debugLine="Log(\"ReportDetail: no se encontró bloque JSON co";
anywheresoftware.b4a.keywords.Common.LogImpl("57274529","ReportDetail: no se encontró bloque JSON completo. Snippet: "+_jsontouse.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(_jsontouse.length(),800))),0);
 //BA.debugLineNum = 77;BA.debugLine="ToastMessageShow(\"Formato de reporte inválido (v";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Formato de reporte inválido (ver logs)."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 78;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 82;BA.debugLine="Dim sb As StringBuilder";
_sb = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 83;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 84;BA.debugLine="For i = 0 To extracted.Length - 1";
{
final int step34 = 1;
final int limit34 = (int) (_extracted.length()-1);
_i = (int) (0) ;
for (;_i <= limit34 ;_i = _i + step34 ) {
 //BA.debugLineNum = 85;BA.debugLine="Dim ch As String = extracted.SubString2(i, i + 1";
_ch = _extracted.substring(_i,(int) (_i+1));
 //BA.debugLineNum = 86;BA.debugLine="Dim b() As Byte = ch.GetBytes(\"UTF8\")";
_b = _ch.getBytes("UTF8");
 //BA.debugLineNum = 87;BA.debugLine="Dim code As Int = 32";
_code = (int) (32);
 //BA.debugLineNum = 88;BA.debugLine="If b.Length > 0 Then";
if (_b.length>0) { 
 //BA.debugLineNum = 89;BA.debugLine="code = b(0)";
_code = (int) (_b[(int) (0)]);
 //BA.debugLineNum = 90;BA.debugLine="If code < 0 Then code = code + 256";
if (_code<0) { 
_code = (int) (_code+256);};
 };
 //BA.debugLineNum = 92;BA.debugLine="If code >= 32 Or code = 9 Or code = 10 Or code =";
if (_code>=32 || _code==9 || _code==10 || _code==13) { 
 //BA.debugLineNum = 93;BA.debugLine="sb.Append(ch)";
_sb.Append(_ch);
 };
 }
};
 //BA.debugLineNum = 96;BA.debugLine="Dim cleaned As String = sb.ToString";
_cleaned = _sb.ToString();
 //BA.debugLineNum = 99;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 100;BA.debugLine="Dim root As Object";
_root = new Object();
 //BA.debugLineNum = 101;BA.debugLine="Try";
try { //BA.debugLineNum = 102;BA.debugLine="parser.Initialize(cleaned)";
_parser.Initialize(_cleaned);
 //BA.debugLineNum = 103;BA.debugLine="If cleaned.StartsWith(\"{\") Then";
if (_cleaned.startsWith("{")) { 
 //BA.debugLineNum = 104;BA.debugLine="root = parser.NextObject";
_root = (Object)(_parser.NextObject().getObject());
 //BA.debugLineNum = 105;BA.debugLine="Log(\"ReportDetail: Parsed cleaned as JSON Objec";
anywheresoftware.b4a.keywords.Common.LogImpl("57274558","ReportDetail: Parsed cleaned as JSON Object",0);
 }else if(_cleaned.startsWith("[")) { 
 //BA.debugLineNum = 107;BA.debugLine="root = parser.NextArray";
_root = (Object)(_parser.NextArray().getObject());
 //BA.debugLineNum = 108;BA.debugLine="Log(\"ReportDetail: Parsed cleaned as JSON Array";
anywheresoftware.b4a.keywords.Common.LogImpl("57274561","ReportDetail: Parsed cleaned as JSON Array",0);
 }else {
 //BA.debugLineNum = 110;BA.debugLine="Log(\"ReportDetail: cleaned no empieza con { ni";
anywheresoftware.b4a.keywords.Common.LogImpl("57274563","ReportDetail: cleaned no empieza con { ni [",0);
 //BA.debugLineNum = 111;BA.debugLine="root = Null";
_root = anywheresoftware.b4a.keywords.Common.Null;
 };
 } 
       catch (Exception e62) {
			processBA.setLastException(e62); //BA.debugLineNum = 114;BA.debugLine="Log(\"ReportDetail: parse error: \" & LastExceptio";
anywheresoftware.b4a.keywords.Common.LogImpl("57274567","ReportDetail: parse error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 115;BA.debugLine="root = Null";
_root = anywheresoftware.b4a.keywords.Common.Null;
 };
 //BA.debugLineNum = 118;BA.debugLine="If root = Null Then";
if (_root== null) { 
 //BA.debugLineNum = 119;BA.debugLine="Try";
try { //BA.debugLineNum = 120;BA.debugLine="Dim snippet As String = cleaned";
_snippet = _cleaned;
 //BA.debugLineNum = 121;BA.debugLine="If snippet.Length > 2000 Then snippet = snippet";
if (_snippet.length()>2000) { 
_snippet = _snippet.substring((int) (0),(int) (2000));};
 //BA.debugLineNum = 122;BA.debugLine="Log(\"ReportDetail: FINAL_PARSE_FAILED_SNIPPET:";
anywheresoftware.b4a.keywords.Common.LogImpl("57274575","ReportDetail: FINAL_PARSE_FAILED_SNIPPET: "+_snippet,0);
 } 
       catch (Exception e71) {
			processBA.setLastException(e71); };
 //BA.debugLineNum = 125;BA.debugLine="ToastMessageShow(\"No se pudo parsear detalle (ve";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se pudo parsear detalle (ver Logcat)."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 126;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 130;BA.debugLine="Dim rp As Map";
_rp = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 131;BA.debugLine="rp = Null";
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 132;BA.debugLine="If root Is Map Then";
if (_root instanceof java.util.Map) { 
 //BA.debugLineNum = 133;BA.debugLine="rp = root";
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_root));
 }else if(_root instanceof java.util.List) { 
 //BA.debugLineNum = 135;BA.debugLine="Dim lst As List = root";
_lst = new anywheresoftware.b4a.objects.collections.List();
_lst = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root));
 //BA.debugLineNum = 136;BA.debugLine="If lst.Size > 0 And lst.Get(0) Is Map Then";
if (_lst.getSize()>0 && _lst.Get((int) (0)) instanceof java.util.Map) { 
 //BA.debugLineNum = 137;BA.debugLine="rp = lst.Get(0)";
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_lst.Get((int) (0))));
 }else {
 //BA.debugLineNum = 139;BA.debugLine="Log(\"ReportDetail: JSON lista sin objetos Map.\"";
anywheresoftware.b4a.keywords.Common.LogImpl("57274592","ReportDetail: JSON lista sin objetos Map.",0);
 //BA.debugLineNum = 140;BA.debugLine="ToastMessageShow(\"Formato de reporte inesperado";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Formato de reporte inesperado."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 141;BA.debugLine="Return";
if (true) return "";
 };
 }else {
 //BA.debugLineNum = 144;BA.debugLine="Log(\"ReportDetail: root no es Map ni List.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("57274597","ReportDetail: root no es Map ni List.",0);
 //BA.debugLineNum = 145;BA.debugLine="ToastMessageShow(\"Formato de reporte inesperado.";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Formato de reporte inesperado."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 146;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 150;BA.debugLine="Try";
try { //BA.debugLineNum = 151;BA.debugLine="If rp.ContainsKey(\"parsed\") Then";
if (_rp.ContainsKey((Object)("parsed"))) { 
 //BA.debugLineNum = 152;BA.debugLine="Dim maybeParsed As Object = rp.Get(\"parsed\")";
_maybeparsed = _rp.Get((Object)("parsed"));
 //BA.debugLineNum = 153;BA.debugLine="If maybeParsed Is Map Then rp = maybeParsed";
if (_maybeparsed instanceof java.util.Map) { 
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_maybeparsed));};
 };
 } 
       catch (Exception e99) {
			processBA.setLastException(e99); };
 //BA.debugLineNum = 160;BA.debugLine="Try";
try { //BA.debugLineNum = 161;BA.debugLine="If rp.ContainsKey(\"area\") = False And rp.Contain";
if (_rp.ContainsKey((Object)("area"))==anywheresoftware.b4a.keywords.Common.False && _rp.ContainsKey((Object)("JSON_Reporte"))) { 
 //BA.debugLineNum = 162;BA.debugLine="Dim sj As String = rp.Get(\"JSON_Reporte\")";
_sj = BA.ObjectToString(_rp.Get((Object)("JSON_Reporte")));
 //BA.debugLineNum = 163;BA.debugLine="If sj <> \"\" Then";
if ((_sj).equals("") == false) { 
 //BA.debugLineNum = 164;BA.debugLine="Dim p2 As JSONParser";
_p2 = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 165;BA.debugLine="p2.Initialize(sj)";
_p2.Initialize(_sj);
 //BA.debugLineNum = 166;BA.debugLine="Dim inner As Object = p2.NextValue";
_inner = _p2.NextValue();
 //BA.debugLineNum = 167;BA.debugLine="If inner Is Map Then rp = inner";
if (_inner instanceof java.util.Map) { 
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_inner));};
 };
 };
 } 
       catch (Exception e111) {
			processBA.setLastException(e111); //BA.debugLineNum = 171;BA.debugLine="Log(\"ReportDetail: no se pudo parsear rp.JSON_Re";
anywheresoftware.b4a.keywords.Common.LogImpl("57274624","ReportDetail: no se pudo parsear rp.JSON_Reporte interno: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 175;BA.debugLine="Try";
try { //BA.debugLineNum = 176;BA.debugLine="RenderReport(rp)";
_renderreport(_rp);
 } 
       catch (Exception e116) {
			processBA.setLastException(e116); //BA.debugLineNum = 178;BA.debugLine="Log(\"ReportDetail: error en RenderReport: \" & La";
anywheresoftware.b4a.keywords.Common.LogImpl("57274631","ReportDetail: error en RenderReport: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 179;BA.debugLine="ToastMessageShow(\"No se pudo mostrar el reporte";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se pudo mostrar el reporte (ver logs)."),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 181;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 10;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 11;BA.debugLine="Public ReportJson As String ' opcional: si se asi";
_reportjson = "";
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public static String  _renderreport(anywheresoftware.b4a.objects.collections.Map _rp) throws Exception{
int _y = 0;
int _padding = 0;
int _w = 0;
anywheresoftware.b4a.objects.collections.Map _area = null;
anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
String _name = "";
int _htitle = 0;
String _desc = "";
anywheresoftware.b4a.objects.LabelWrapper _lbldesc = null;
int _hdesc = 0;
anywheresoftware.b4a.objects.collections.List _cars = null;
int _i = 0;
anywheresoftware.b4a.objects.collections.Map _c = null;
String _carname = "";
anywheresoftware.b4a.objects.PanelWrapper _card = null;
anywheresoftware.b4a.objects.LabelWrapper _lblcar = null;
int _hcarname = 0;
String _responsestext = "";
anywheresoftware.b4a.objects.collections.Map _respmap = null;
String _k = "";
Object _v = null;
String _extras = "";
String _details = "";
anywheresoftware.b4a.objects.LabelWrapper _lblresp = null;
int _hresp = 0;
int _hcard = 0;
Object _rawobj = null;
String _rawtext = "";
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _jg = null;
anywheresoftware.b4a.objects.LabelWrapper _lblraw = null;
int _hraw = 0;
 //BA.debugLineNum = 246;BA.debugLine="Sub RenderReport(rp As Map)";
 //BA.debugLineNum = 247;BA.debugLine="Dim y As Int = 10dip";
_y = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10));
 //BA.debugLineNum = 248;BA.debugLine="Dim padding As Int = 10dip";
_padding = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10));
 //BA.debugLineNum = 249;BA.debugLine="Dim w As Int = 100%x - 2 * padding";
_w = (int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-2*_padding);
 //BA.debugLineNum = 252;BA.debugLine="If rp.ContainsKey(\"area\") Then";
if (_rp.ContainsKey((Object)("area"))) { 
 //BA.debugLineNum = 253;BA.debugLine="Try";
try { //BA.debugLineNum = 254;BA.debugLine="Dim area As Map = rp.Get(\"area\")";
_area = new anywheresoftware.b4a.objects.collections.Map();
_area = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_rp.Get((Object)("area"))));
 //BA.debugLineNum = 255;BA.debugLine="Dim lblTitle As Label";
_lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 256;BA.debugLine="lblTitle.Initialize(\"lblTitle\")";
_lbltitle.Initialize(mostCurrent.activityBA,"lblTitle");
 //BA.debugLineNum = 257;BA.debugLine="Dim name As String = \"\"";
_name = "";
 //BA.debugLineNum = 258;BA.debugLine="If area.ContainsKey(\"area_name\") Then name = ar";
if (_area.ContainsKey((Object)("area_name"))) { 
_name = BA.ObjectToString(_area.Get((Object)("area_name")));};
 //BA.debugLineNum = 259;BA.debugLine="If name = \"\" And area.ContainsKey(\"areaName\") T";
if ((_name).equals("") && _area.ContainsKey((Object)("areaName"))) { 
_name = BA.ObjectToString(_area.Get((Object)("areaName")));};
 //BA.debugLineNum = 260;BA.debugLine="lblTitle.Text = \"Área: \" & name";
_lbltitle.setText(BA.ObjectToCharSequence("Área: "+_name));
 //BA.debugLineNum = 261;BA.debugLine="lblTitle.TextSize = 16";
_lbltitle.setTextSize((float) (16));
 //BA.debugLineNum = 262;BA.debugLine="lblTitle.Typeface = Typeface.DEFAULT_BOLD";
_lbltitle.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 263;BA.debugLine="Dim hTitle As Int = EstimateLabelHeight(lblTitl";
_htitle = _estimatelabelheight(_lbltitle.getText(),(int) (_lbltitle.getTextSize()),_w);
 //BA.debugLineNum = 264;BA.debugLine="pnl.AddView(lblTitle, padding, y, w, hTitle)";
mostCurrent._pnl.AddView((android.view.View)(_lbltitle.getObject()),_padding,_y,_w,_htitle);
 //BA.debugLineNum = 265;BA.debugLine="y = y + hTitle + 6dip";
_y = (int) (_y+_htitle+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 //BA.debugLineNum = 267;BA.debugLine="Dim desc As String = \"\"";
_desc = "";
 //BA.debugLineNum = 268;BA.debugLine="If area.ContainsKey(\"area_description\") Then de";
if (_area.ContainsKey((Object)("area_description"))) { 
_desc = BA.ObjectToString(_area.Get((Object)("area_description")));};
 //BA.debugLineNum = 269;BA.debugLine="If desc = \"\" And area.ContainsKey(\"areaDescript";
if ((_desc).equals("") && _area.ContainsKey((Object)("areaDescription"))) { 
_desc = BA.ObjectToString(_area.Get((Object)("areaDescription")));};
 //BA.debugLineNum = 270;BA.debugLine="If desc <> \"\" Then";
if ((_desc).equals("") == false) { 
 //BA.debugLineNum = 271;BA.debugLine="Dim lblDesc As Label";
_lbldesc = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 272;BA.debugLine="lblDesc.Initialize(\"lblDesc\")";
_lbldesc.Initialize(mostCurrent.activityBA,"lblDesc");
 //BA.debugLineNum = 273;BA.debugLine="lblDesc.Text = \"Descripción: \" & desc";
_lbldesc.setText(BA.ObjectToCharSequence("Descripción: "+_desc));
 //BA.debugLineNum = 274;BA.debugLine="lblDesc.TextSize = 14";
_lbldesc.setTextSize((float) (14));
 //BA.debugLineNum = 275;BA.debugLine="Dim hDesc As Int = EstimateLabelHeight(lblDesc";
_hdesc = _estimatelabelheight(_lbldesc.getText(),(int) (_lbldesc.getTextSize()),_w);
 //BA.debugLineNum = 276;BA.debugLine="pnl.AddView(lblDesc, padding, y, w, hDesc)";
mostCurrent._pnl.AddView((android.view.View)(_lbldesc.getObject()),_padding,_y,_w,_hdesc);
 //BA.debugLineNum = 277;BA.debugLine="y = y + hDesc + 6dip";
_y = (int) (_y+_hdesc+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 };
 } 
       catch (Exception e31) {
			processBA.setLastException(e31); //BA.debugLineNum = 280;BA.debugLine="Log(\"Error leyendo campo area: \" & LastExceptio";
anywheresoftware.b4a.keywords.Common.LogImpl("52359330","Error leyendo campo area: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 285;BA.debugLine="If rp.ContainsKey(\"car_reports\") Then";
if (_rp.ContainsKey((Object)("car_reports"))) { 
 //BA.debugLineNum = 286;BA.debugLine="Try";
try { //BA.debugLineNum = 287;BA.debugLine="Dim cars As List = rp.Get(\"car_reports\")";
_cars = new anywheresoftware.b4a.objects.collections.List();
_cars = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_rp.Get((Object)("car_reports"))));
 //BA.debugLineNum = 288;BA.debugLine="For i = 0 To cars.Size - 1";
{
final int step37 = 1;
final int limit37 = (int) (_cars.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit37 ;_i = _i + step37 ) {
 //BA.debugLineNum = 289;BA.debugLine="Dim c As Map = cars.Get(i)";
_c = new anywheresoftware.b4a.objects.collections.Map();
_c = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_cars.Get(_i)));
 //BA.debugLineNum = 290;BA.debugLine="Dim carName As String = \"\"";
_carname = "";
 //BA.debugLineNum = 291;BA.debugLine="If c.ContainsKey(\"car_name\") Then carName = c.";
if (_c.ContainsKey((Object)("car_name"))) { 
_carname = BA.ObjectToString(_c.Get((Object)("car_name")));};
 //BA.debugLineNum = 294;BA.debugLine="Dim card As Panel";
_card = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 295;BA.debugLine="card.Initialize(\"card\" & i)";
_card.Initialize(mostCurrent.activityBA,"card"+BA.NumberToString(_i));
 //BA.debugLineNum = 296;BA.debugLine="card.Color = Colors.ARGB(255, 245, 245, 245)";
_card.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (245),(int) (245),(int) (245)));
 //BA.debugLineNum = 297;BA.debugLine="pnl.AddView(card, padding, y, w, 60dip) ' altu";
mostCurrent._pnl.AddView((android.view.View)(_card.getObject()),_padding,_y,_w,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 300;BA.debugLine="Dim lblCar As Label";
_lblcar = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 301;BA.debugLine="lblCar.Initialize(\"lblCar\" & i)";
_lblcar.Initialize(mostCurrent.activityBA,"lblCar"+BA.NumberToString(_i));
 //BA.debugLineNum = 302;BA.debugLine="lblCar.Text = \"CAR: \" & carName";
_lblcar.setText(BA.ObjectToCharSequence("CAR: "+_carname));
 //BA.debugLineNum = 303;BA.debugLine="lblCar.TextSize = 15";
_lblcar.setTextSize((float) (15));
 //BA.debugLineNum = 304;BA.debugLine="lblCar.Typeface = Typeface.DEFAULT_BOLD";
_lblcar.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 305;BA.debugLine="Dim hCarName As Int = EstimateLabelHeight(lblC";
_hcarname = _estimatelabelheight(_lblcar.getText(),(int) (_lblcar.getTextSize()),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))));
 //BA.debugLineNum = 306;BA.debugLine="card.AddView(lblCar, 8dip, 4dip, w - 16dip, hC";
_card.AddView((android.view.View)(_lblcar.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))),_hcarname);
 //BA.debugLineNum = 309;BA.debugLine="Dim responsesText As String = \"\"";
_responsestext = "";
 //BA.debugLineNum = 310;BA.debugLine="If c.ContainsKey(\"responses\") Then";
if (_c.ContainsKey((Object)("responses"))) { 
 //BA.debugLineNum = 311;BA.debugLine="Try";
try { //BA.debugLineNum = 312;BA.debugLine="Dim respMap As Map = c.Get(\"responses\")";
_respmap = new anywheresoftware.b4a.objects.collections.Map();
_respmap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_c.Get((Object)("responses"))));
 //BA.debugLineNum = 313;BA.debugLine="For Each k As String In respMap.Keys";
{
final anywheresoftware.b4a.BA.IterableList group56 = _respmap.Keys();
final int groupLen56 = group56.getSize()
;int index56 = 0;
;
for (; index56 < groupLen56;index56++){
_k = BA.ObjectToString(group56.Get(index56));
 //BA.debugLineNum = 314;BA.debugLine="Try";
try { //BA.debugLineNum = 315;BA.debugLine="Dim v As Object = respMap.Get(k)";
_v = _respmap.Get((Object)(_k));
 //BA.debugLineNum = 316;BA.debugLine="responsesText = responsesText & k & \": \" &";
_responsestext = _responsestext+_k+": "+BA.ObjectToString(_v)+anywheresoftware.b4a.keywords.Common.CRLF;
 } 
       catch (Exception e61) {
			processBA.setLastException(e61); //BA.debugLineNum = 318;BA.debugLine="Log(\"RenderReport: error leyendo responses";
anywheresoftware.b4a.keywords.Common.LogImpl("52359368","RenderReport: error leyendo responses key '"+_k+"': "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 }
};
 } 
       catch (Exception e65) {
			processBA.setLastException(e65); //BA.debugLineNum = 322;BA.debugLine="responsesText = \"Error leyendo responses\"";
_responsestext = "Error leyendo responses";
 //BA.debugLineNum = 323;BA.debugLine="Log(\"Error reading responses map: \" & LastEx";
anywheresoftware.b4a.keywords.Common.LogImpl("52359373","Error reading responses map: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 328;BA.debugLine="Dim extras As String = \"\"";
_extras = "";
 //BA.debugLineNum = 329;BA.debugLine="If c.ContainsKey(\"observacion\") Then extras =";
if (_c.ContainsKey((Object)("observacion"))) { 
_extras = _extras+"Observación: "+BA.ObjectToString(_c.Get((Object)("observacion")))+anywheresoftware.b4a.keywords.Common.CRLF;};
 //BA.debugLineNum = 330;BA.debugLine="If c.ContainsKey(\"incidencia\") Then extras = e";
if (_c.ContainsKey((Object)("incidencia"))) { 
_extras = _extras+"Incidencia: "+BA.ObjectToString(_c.Get((Object)("incidencia")))+anywheresoftware.b4a.keywords.Common.CRLF;};
 //BA.debugLineNum = 332;BA.debugLine="Dim details As String = \"\"";
_details = "";
 //BA.debugLineNum = 333;BA.debugLine="If responsesText <> \"\" Then details = details";
if ((_responsestext).equals("") == false) { 
_details = _details+_responsestext;};
 //BA.debugLineNum = 334;BA.debugLine="If extras <> \"\" Then details = details & extra";
if ((_extras).equals("") == false) { 
_details = _details+_extras;};
 //BA.debugLineNum = 335;BA.debugLine="If details = \"\" Then details = \"(sin detalles)";
if ((_details).equals("")) { 
_details = "(sin detalles)";};
 //BA.debugLineNum = 337;BA.debugLine="Dim lblResp As Label";
_lblresp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 338;BA.debugLine="lblResp.Initialize(\"lblResp\" & i)";
_lblresp.Initialize(mostCurrent.activityBA,"lblResp"+BA.NumberToString(_i));
 //BA.debugLineNum = 339;BA.debugLine="lblResp.Text = details";
_lblresp.setText(BA.ObjectToCharSequence(_details));
 //BA.debugLineNum = 340;BA.debugLine="lblResp.TextSize = 13";
_lblresp.setTextSize((float) (13));
 //BA.debugLineNum = 341;BA.debugLine="Dim hResp As Int = EstimateLabelHeight(lblResp";
_hresp = _estimatelabelheight(_lblresp.getText(),(int) (_lblresp.getTextSize()),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))));
 //BA.debugLineNum = 343;BA.debugLine="card.AddView(lblResp, 8dip, 4dip + hCarName, w";
_card.AddView((android.view.View)(_lblresp.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))+_hcarname),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))),_hresp);
 //BA.debugLineNum = 346;BA.debugLine="Dim hCard As Int = hCarName + hResp + 12dip";
_hcard = (int) (_hcarname+_hresp+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (12)));
 //BA.debugLineNum = 347;BA.debugLine="card.SetLayout(card.Left, card.Top, card.Width";
_card.SetLayout(_card.getLeft(),_card.getTop(),_card.getWidth(),_hcard);
 //BA.debugLineNum = 348;BA.debugLine="y = y + hCard + 8dip";
_y = (int) (_y+_hcard+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)));
 }
};
 } 
       catch (Exception e87) {
			processBA.setLastException(e87); //BA.debugLineNum = 351;BA.debugLine="Log(\"Error leyendo car_reports: \" & LastExcepti";
anywheresoftware.b4a.keywords.Common.LogImpl("52359401","Error leyendo car_reports: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 }else {
 //BA.debugLineNum = 355;BA.debugLine="If rp.ContainsKey(\"raw\") Then";
if (_rp.ContainsKey((Object)("raw"))) { 
 //BA.debugLineNum = 356;BA.debugLine="Try";
try { //BA.debugLineNum = 357;BA.debugLine="Dim rawObj As Object = rp.Get(\"raw\")";
_rawobj = _rp.Get((Object)("raw"));
 //BA.debugLineNum = 358;BA.debugLine="Dim rawText As String = \"\"";
_rawtext = "";
 //BA.debugLineNum = 359;BA.debugLine="If rawObj Is Map Then";
if (_rawobj instanceof java.util.Map) { 
 //BA.debugLineNum = 360;BA.debugLine="Dim jg As JSONGenerator";
_jg = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 361;BA.debugLine="jg.Initialize(rawObj)";
_jg.Initialize((anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_rawobj)));
 //BA.debugLineNum = 362;BA.debugLine="rawText = jg.ToString";
_rawtext = _jg.ToString();
 }else {
 //BA.debugLineNum = 364;BA.debugLine="rawText = rawObj";
_rawtext = BA.ObjectToString(_rawobj);
 };
 //BA.debugLineNum = 366;BA.debugLine="Dim lblRaw As Label";
_lblraw = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 367;BA.debugLine="lblRaw.Initialize(\"lblRaw\")";
_lblraw.Initialize(mostCurrent.activityBA,"lblRaw");
 //BA.debugLineNum = 368;BA.debugLine="lblRaw.Text = rawText";
_lblraw.setText(BA.ObjectToCharSequence(_rawtext));
 //BA.debugLineNum = 369;BA.debugLine="lblRaw.TextSize = 13";
_lblraw.setTextSize((float) (13));
 //BA.debugLineNum = 370;BA.debugLine="Dim hRaw As Int = EstimateLabelHeight(lblRaw.T";
_hraw = _estimatelabelheight(_lblraw.getText(),(int) (_lblraw.getTextSize()),_w);
 //BA.debugLineNum = 371;BA.debugLine="pnl.AddView(lblRaw, 10dip, y, w, hRaw)";
mostCurrent._pnl.AddView((android.view.View)(_lblraw.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),_y,_w,_hraw);
 //BA.debugLineNum = 372;BA.debugLine="y = y + hRaw + 6dip";
_y = (int) (_y+_hraw+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 } 
       catch (Exception e109) {
			processBA.setLastException(e109); //BA.debugLineNum = 374;BA.debugLine="Log(\"Error mostrando raw: \" & LastException.Me";
anywheresoftware.b4a.keywords.Common.LogImpl("52359424","Error mostrando raw: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 };
 //BA.debugLineNum = 379;BA.debugLine="pnl.Height = y + 20dip";
mostCurrent._pnl.setHeight((int) (_y+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))));
 //BA.debugLineNum = 380;BA.debugLine="End Sub";
return "";
}
}
