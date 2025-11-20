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
public b4a.example.reportdialog _reportdialog = null;
public b4a.example.login _login = null;
public b4a.example.menuprincipal_maquilas _menuprincipal_maquilas = null;
public b4a.example.reportsbyarea _reportsbyarea = null;
public b4a.example.httputils2service _httputils2service = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
String _jsontouse = "";
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
Object _root = null;
anywheresoftware.b4a.objects.collections.Map _rp = null;
anywheresoftware.b4a.objects.LabelWrapper _lblerr = null;
 //BA.debugLineNum = 19;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 20;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 21;BA.debugLine="Activity.Title = \"Detalle reporte\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Detalle reporte"));
 //BA.debugLineNum = 24;BA.debugLine="sv.Initialize(100%y)";
mostCurrent._sv.Initialize(mostCurrent.activityBA,anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 25;BA.debugLine="Activity.AddView(sv, 0, 0, 100%x, 100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._sv.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 26;BA.debugLine="pnl = sv.Panel";
mostCurrent._pnl = mostCurrent._sv.getPanel();
 //BA.debugLineNum = 27;BA.debugLine="pnl.RemoveAllViews";
mostCurrent._pnl.RemoveAllViews();
 //BA.debugLineNum = 29;BA.debugLine="Dim jsonToUse As String = ReportJson";
_jsontouse = _reportjson;
 //BA.debugLineNum = 30;BA.debugLine="If jsonToUse = \"\" Then";
if ((_jsontouse).equals("")) { 
 //BA.debugLineNum = 32;BA.debugLine="Try";
try { //BA.debugLineNum = 33;BA.debugLine="If File.Exists(File.DirInternal, \"current_repor";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json")) { 
 //BA.debugLineNum = 34;BA.debugLine="jsonToUse = File.ReadString(File.DirInternal,";
_jsontouse = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json");
 };
 } 
       catch (Exception e14) {
			processBA.setLastException(e14); //BA.debugLineNum = 37;BA.debugLine="Log(\"Error reading current_report.json: \" & Las";
anywheresoftware.b4a.keywords.Common.LogImpl("78126482","Error reading current_report.json: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 41;BA.debugLine="If jsonToUse = \"\" Then";
if ((_jsontouse).equals("")) { 
 //BA.debugLineNum = 42;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 43;BA.debugLine="lbl.Initialize(\"lbl\")";
_lbl.Initialize(mostCurrent.activityBA,"lbl");
 //BA.debugLineNum = 44;BA.debugLine="lbl.Text = \"No hay datos para mostrar.\"";
_lbl.setText(BA.ObjectToCharSequence("No hay datos para mostrar."));
 //BA.debugLineNum = 45;BA.debugLine="lbl.TextSize = 16";
_lbl.setTextSize((float) (16));
 //BA.debugLineNum = 46;BA.debugLine="pnl.AddView(lbl, 10dip, 10dip, 100%x - 20dip, Es";
mostCurrent._pnl.AddView((android.view.View)(_lbl.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),_estimatelabelheight(_lbl.getText(),(int) (_lbl.getTextSize()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)))));
 //BA.debugLineNum = 47;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 50;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 51;BA.debugLine="parser.Initialize(jsonToUse)";
_parser.Initialize(_jsontouse);
 //BA.debugLineNum = 52;BA.debugLine="Try";
try { //BA.debugLineNum = 53;BA.debugLine="Dim root As Object = parser.NextValue";
_root = _parser.NextValue();
 //BA.debugLineNum = 54;BA.debugLine="If root Is Map Then";
if (_root instanceof java.util.Map) { 
 //BA.debugLineNum = 55;BA.debugLine="Dim rp As Map = root";
_rp = new anywheresoftware.b4a.objects.collections.Map();
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_root));
 //BA.debugLineNum = 56;BA.debugLine="RenderReport(rp)";
_renderreport(_rp);
 }else {
 //BA.debugLineNum = 58;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 59;BA.debugLine="lbl.Initialize(\"lbl2\")";
_lbl.Initialize(mostCurrent.activityBA,"lbl2");
 //BA.debugLineNum = 60;BA.debugLine="lbl.Text = \"Formato de reporte inesperado.\"";
_lbl.setText(BA.ObjectToCharSequence("Formato de reporte inesperado."));
 //BA.debugLineNum = 61;BA.debugLine="pnl.AddView(lbl, 10dip, 10dip, 100%x - 20dip, E";
mostCurrent._pnl.AddView((android.view.View)(_lbl.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),_estimatelabelheight(_lbl.getText(),(int) (_lbl.getTextSize()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)))));
 };
 } 
       catch (Exception e39) {
			processBA.setLastException(e39); //BA.debugLineNum = 64;BA.debugLine="Dim lblErr As Label";
_lblerr = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 65;BA.debugLine="lblErr.Initialize(\"lblErr\")";
_lblerr.Initialize(mostCurrent.activityBA,"lblErr");
 //BA.debugLineNum = 66;BA.debugLine="lblErr.Text = \"Error parseando reporte: \" & Last";
_lblerr.setText(BA.ObjectToCharSequence("Error parseando reporte: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()));
 //BA.debugLineNum = 67;BA.debugLine="pnl.AddView(lblErr, 10dip, 10dip, 100%x - 20dip,";
mostCurrent._pnl.AddView((android.view.View)(_lblerr.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),_estimatelabelheight(_lblerr.getText(),(int) (_lblerr.getTextSize()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)))));
 //BA.debugLineNum = 68;BA.debugLine="Log(\"Error parseando ReportDetail JSON: \" & Last";
anywheresoftware.b4a.keywords.Common.LogImpl("78126513","Error parseando ReportDetail JSON: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 76;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 72;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static int  _estimatelabelheight(String _text,int _textsize,int _width) throws Exception{
int _charwidth = 0;
int _charsperline = 0;
int _textlen = 0;
int _lines = 0;
int _lineheight = 0;
int _result = 0;
 //BA.debugLineNum = 203;BA.debugLine="Sub EstimateLabelHeight(text As String, textSize A";
 //BA.debugLineNum = 204;BA.debugLine="If text = \"\" Then Return 30dip";
if ((_text).equals("")) { 
if (true) return anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30));};
 //BA.debugLineNum = 205;BA.debugLine="If width <= 0 Then width = 100dip";
if (_width<=0) { 
_width = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100));};
 //BA.debugLineNum = 207;BA.debugLine="Dim charWidth As Int = (textSize * 6) / 10";
_charwidth = (int) ((_textsize*6)/(double)10);
 //BA.debugLineNum = 208;BA.debugLine="If charWidth < 4 Then charWidth = 4 ' mínimo razo";
if (_charwidth<4) { 
_charwidth = (int) (4);};
 //BA.debugLineNum = 209;BA.debugLine="Dim charsPerLine As Int = width / charWidth";
_charsperline = (int) (_width/(double)_charwidth);
 //BA.debugLineNum = 210;BA.debugLine="If charsPerLine < 1 Then charsPerLine = 1";
if (_charsperline<1) { 
_charsperline = (int) (1);};
 //BA.debugLineNum = 211;BA.debugLine="Dim textLen As Int = text.Length";
_textlen = _text.length();
 //BA.debugLineNum = 212;BA.debugLine="Dim lines As Int = textLen / charsPerLine";
_lines = (int) (_textlen/(double)_charsperline);
 //BA.debugLineNum = 213;BA.debugLine="If (textLen Mod charsPerLine) <> 0 Then lines = l";
if ((_textlen%_charsperline)!=0) { 
_lines = (int) (_lines+1);};
 //BA.debugLineNum = 215;BA.debugLine="Dim lineHeight As Int = (textSize * 16) / 10";
_lineheight = (int) ((_textsize*16)/(double)10);
 //BA.debugLineNum = 216;BA.debugLine="If lineHeight < 14 Then lineHeight = 14";
if (_lineheight<14) { 
_lineheight = (int) (14);};
 //BA.debugLineNum = 217;BA.debugLine="Dim result As Int = lines * lineHeight + 6dip";
_result = (int) (_lines*_lineheight+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 //BA.debugLineNum = 218;BA.debugLine="Return result";
if (true) return _result;
 //BA.debugLineNum = 219;BA.debugLine="End Sub";
return 0;
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
anywheresoftware.b4a.objects.collections.List _keys = null;
String _k = "";
Object _v = null;
String _extras = "";
String _details = "";
anywheresoftware.b4a.objects.LabelWrapper _lblresp = null;
int _hresp = 0;
int _hcard = 0;
anywheresoftware.b4a.objects.LabelWrapper _lblraw = null;
int _hraw = 0;
 //BA.debugLineNum = 81;BA.debugLine="Sub RenderReport(rp As Map)";
 //BA.debugLineNum = 82;BA.debugLine="Dim y As Int = 10dip";
_y = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10));
 //BA.debugLineNum = 83;BA.debugLine="Dim padding As Int = 10dip";
_padding = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10));
 //BA.debugLineNum = 84;BA.debugLine="Dim w As Int = 100%x - 2 * padding";
_w = (int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-2*_padding);
 //BA.debugLineNum = 87;BA.debugLine="If rp.ContainsKey(\"area\") Then";
if (_rp.ContainsKey((Object)("area"))) { 
 //BA.debugLineNum = 88;BA.debugLine="Try";
try { //BA.debugLineNum = 89;BA.debugLine="Dim area As Map = rp.Get(\"area\")";
_area = new anywheresoftware.b4a.objects.collections.Map();
_area = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_rp.Get((Object)("area"))));
 //BA.debugLineNum = 90;BA.debugLine="Dim lblTitle As Label";
_lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 91;BA.debugLine="lblTitle.Initialize(\"lblTitle\")";
_lbltitle.Initialize(mostCurrent.activityBA,"lblTitle");
 //BA.debugLineNum = 92;BA.debugLine="Dim name As String = \"\"";
_name = "";
 //BA.debugLineNum = 93;BA.debugLine="If area.ContainsKey(\"area_name\") Then name = ar";
if (_area.ContainsKey((Object)("area_name"))) { 
_name = BA.ObjectToString(_area.Get((Object)("area_name")));};
 //BA.debugLineNum = 94;BA.debugLine="If name = \"\" And area.ContainsKey(\"areaName\") T";
if ((_name).equals("") && _area.ContainsKey((Object)("areaName"))) { 
_name = BA.ObjectToString(_area.Get((Object)("areaName")));};
 //BA.debugLineNum = 95;BA.debugLine="lblTitle.Text = \"Área: \" & name";
_lbltitle.setText(BA.ObjectToCharSequence("Área: "+_name));
 //BA.debugLineNum = 96;BA.debugLine="lblTitle.TextSize = 16";
_lbltitle.setTextSize((float) (16));
 //BA.debugLineNum = 97;BA.debugLine="lblTitle.Typeface = Typeface.DEFAULT_BOLD";
_lbltitle.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 98;BA.debugLine="Dim hTitle As Int = EstimateLabelHeight(lblTitl";
_htitle = _estimatelabelheight(_lbltitle.getText(),(int) (_lbltitle.getTextSize()),_w);
 //BA.debugLineNum = 99;BA.debugLine="pnl.AddView(lblTitle, padding, y, w, hTitle)";
mostCurrent._pnl.AddView((android.view.View)(_lbltitle.getObject()),_padding,_y,_w,_htitle);
 //BA.debugLineNum = 100;BA.debugLine="y = y + hTitle + 6dip";
_y = (int) (_y+_htitle+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 //BA.debugLineNum = 102;BA.debugLine="Dim desc As String = \"\"";
_desc = "";
 //BA.debugLineNum = 103;BA.debugLine="If area.ContainsKey(\"area_description\") Then de";
if (_area.ContainsKey((Object)("area_description"))) { 
_desc = BA.ObjectToString(_area.Get((Object)("area_description")));};
 //BA.debugLineNum = 104;BA.debugLine="If desc = \"\" And area.ContainsKey(\"areaDescript";
if ((_desc).equals("") && _area.ContainsKey((Object)("areaDescription"))) { 
_desc = BA.ObjectToString(_area.Get((Object)("areaDescription")));};
 //BA.debugLineNum = 105;BA.debugLine="If desc <> \"\" Then";
if ((_desc).equals("") == false) { 
 //BA.debugLineNum = 106;BA.debugLine="Dim lblDesc As Label";
_lbldesc = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 107;BA.debugLine="lblDesc.Initialize(\"lblDesc\")";
_lbldesc.Initialize(mostCurrent.activityBA,"lblDesc");
 //BA.debugLineNum = 108;BA.debugLine="lblDesc.Text = \"Descripción: \" & desc";
_lbldesc.setText(BA.ObjectToCharSequence("Descripción: "+_desc));
 //BA.debugLineNum = 109;BA.debugLine="lblDesc.TextSize = 14";
_lbldesc.setTextSize((float) (14));
 //BA.debugLineNum = 110;BA.debugLine="Dim hDesc As Int = EstimateLabelHeight(lblDesc";
_hdesc = _estimatelabelheight(_lbldesc.getText(),(int) (_lbldesc.getTextSize()),_w);
 //BA.debugLineNum = 111;BA.debugLine="pnl.AddView(lblDesc, padding, y, w, hDesc)";
mostCurrent._pnl.AddView((android.view.View)(_lbldesc.getObject()),_padding,_y,_w,_hdesc);
 //BA.debugLineNum = 112;BA.debugLine="y = y + hDesc + 6dip";
_y = (int) (_y+_hdesc+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 };
 } 
       catch (Exception e31) {
			processBA.setLastException(e31); //BA.debugLineNum = 115;BA.debugLine="Log(\"Error leyendo campo area: \" & LastExceptio";
anywheresoftware.b4a.keywords.Common.LogImpl("78323106","Error leyendo campo area: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 120;BA.debugLine="If rp.ContainsKey(\"car_reports\") Then";
if (_rp.ContainsKey((Object)("car_reports"))) { 
 //BA.debugLineNum = 121;BA.debugLine="Try";
try { //BA.debugLineNum = 122;BA.debugLine="Dim cars As List = rp.Get(\"car_reports\")";
_cars = new anywheresoftware.b4a.objects.collections.List();
_cars = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_rp.Get((Object)("car_reports"))));
 //BA.debugLineNum = 123;BA.debugLine="For i = 0 To cars.Size - 1";
{
final int step37 = 1;
final int limit37 = (int) (_cars.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit37 ;_i = _i + step37 ) {
 //BA.debugLineNum = 124;BA.debugLine="Dim c As Map = cars.Get(i)";
_c = new anywheresoftware.b4a.objects.collections.Map();
_c = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_cars.Get(_i)));
 //BA.debugLineNum = 125;BA.debugLine="Dim carName As String = \"\"";
_carname = "";
 //BA.debugLineNum = 126;BA.debugLine="If c.ContainsKey(\"car_name\") Then carName = c.";
if (_c.ContainsKey((Object)("car_name"))) { 
_carname = BA.ObjectToString(_c.Get((Object)("car_name")));};
 //BA.debugLineNum = 129;BA.debugLine="Dim card As Panel";
_card = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 130;BA.debugLine="card.Initialize(\"card\" & i)";
_card.Initialize(mostCurrent.activityBA,"card"+BA.NumberToString(_i));
 //BA.debugLineNum = 131;BA.debugLine="card.Color = Colors.ARGB(255, 245, 245, 245)";
_card.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (245),(int) (245),(int) (245)));
 //BA.debugLineNum = 132;BA.debugLine="pnl.AddView(card, padding, y, w, 60dip) ' altu";
mostCurrent._pnl.AddView((android.view.View)(_card.getObject()),_padding,_y,_w,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 135;BA.debugLine="Dim lblCar As Label";
_lblcar = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 136;BA.debugLine="lblCar.Initialize(\"lblCar\" & i)";
_lblcar.Initialize(mostCurrent.activityBA,"lblCar"+BA.NumberToString(_i));
 //BA.debugLineNum = 137;BA.debugLine="lblCar.Text = \"CAR: \" & carName";
_lblcar.setText(BA.ObjectToCharSequence("CAR: "+_carname));
 //BA.debugLineNum = 138;BA.debugLine="lblCar.TextSize = 15";
_lblcar.setTextSize((float) (15));
 //BA.debugLineNum = 139;BA.debugLine="lblCar.Typeface = Typeface.DEFAULT_BOLD";
_lblcar.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 140;BA.debugLine="Dim hCarName As Int = EstimateLabelHeight(lblC";
_hcarname = _estimatelabelheight(_lblcar.getText(),(int) (_lblcar.getTextSize()),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))));
 //BA.debugLineNum = 141;BA.debugLine="card.AddView(lblCar, 8dip, 4dip, w - 16dip, hC";
_card.AddView((android.view.View)(_lblcar.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))),_hcarname);
 //BA.debugLineNum = 144;BA.debugLine="Dim responsesText As String = \"\"";
_responsestext = "";
 //BA.debugLineNum = 145;BA.debugLine="If c.ContainsKey(\"responses\") Then";
if (_c.ContainsKey((Object)("responses"))) { 
 //BA.debugLineNum = 146;BA.debugLine="Try";
try { //BA.debugLineNum = 147;BA.debugLine="Dim respMap As Map = c.Get(\"responses\")";
_respmap = new anywheresoftware.b4a.objects.collections.Map();
_respmap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_c.Get((Object)("responses"))));
 //BA.debugLineNum = 148;BA.debugLine="Dim keys As List = respMap.Keys";
_keys = new anywheresoftware.b4a.objects.collections.List();
_keys = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_respmap.Keys()));
 //BA.debugLineNum = 149;BA.debugLine="For Each k As String In keys";
{
final anywheresoftware.b4a.BA.IterableList group57 = _keys;
final int groupLen57 = group57.getSize()
;int index57 = 0;
;
for (; index57 < groupLen57;index57++){
_k = BA.ObjectToString(group57.Get(index57));
 //BA.debugLineNum = 150;BA.debugLine="Dim v As Object = respMap.Get(k)";
_v = _respmap.Get((Object)(_k));
 //BA.debugLineNum = 151;BA.debugLine="responsesText = responsesText & k & \": \" &";
_responsestext = _responsestext+_k+": "+BA.ObjectToString(_v)+anywheresoftware.b4a.keywords.Common.CRLF;
 }
};
 } 
       catch (Exception e62) {
			processBA.setLastException(e62); //BA.debugLineNum = 154;BA.debugLine="responsesText = \"Error leyendo responses\"";
_responsestext = "Error leyendo responses";
 //BA.debugLineNum = 155;BA.debugLine="Log(\"Error reading responses map: \" & LastEx";
anywheresoftware.b4a.keywords.Common.LogImpl("78323146","Error reading responses map: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 160;BA.debugLine="Dim extras As String = \"\"";
_extras = "";
 //BA.debugLineNum = 161;BA.debugLine="If c.ContainsKey(\"observacion\") Then extras =";
if (_c.ContainsKey((Object)("observacion"))) { 
_extras = _extras+"Observación: "+BA.ObjectToString(_c.Get((Object)("observacion")))+anywheresoftware.b4a.keywords.Common.CRLF;};
 //BA.debugLineNum = 162;BA.debugLine="If c.ContainsKey(\"incidencia\") Then extras = e";
if (_c.ContainsKey((Object)("incidencia"))) { 
_extras = _extras+"Incidencia: "+BA.ObjectToString(_c.Get((Object)("incidencia")))+anywheresoftware.b4a.keywords.Common.CRLF;};
 //BA.debugLineNum = 164;BA.debugLine="Dim details As String = \"\"";
_details = "";
 //BA.debugLineNum = 165;BA.debugLine="If responsesText <> \"\" Then details = details";
if ((_responsestext).equals("") == false) { 
_details = _details+_responsestext;};
 //BA.debugLineNum = 166;BA.debugLine="If extras <> \"\" Then details = details & extra";
if ((_extras).equals("") == false) { 
_details = _details+_extras;};
 //BA.debugLineNum = 167;BA.debugLine="If details = \"\" Then details = \"(sin detalles)";
if ((_details).equals("")) { 
_details = "(sin detalles)";};
 //BA.debugLineNum = 169;BA.debugLine="Dim lblResp As Label";
_lblresp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 170;BA.debugLine="lblResp.Initialize(\"lblResp\" & i)";
_lblresp.Initialize(mostCurrent.activityBA,"lblResp"+BA.NumberToString(_i));
 //BA.debugLineNum = 171;BA.debugLine="lblResp.Text = details";
_lblresp.setText(BA.ObjectToCharSequence(_details));
 //BA.debugLineNum = 172;BA.debugLine="lblResp.TextSize = 13";
_lblresp.setTextSize((float) (13));
 //BA.debugLineNum = 173;BA.debugLine="Dim hResp As Int = EstimateLabelHeight(lblResp";
_hresp = _estimatelabelheight(_lblresp.getText(),(int) (_lblresp.getTextSize()),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))));
 //BA.debugLineNum = 175;BA.debugLine="card.AddView(lblResp, 8dip, 4dip + hCarName, w";
_card.AddView((android.view.View)(_lblresp.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))+_hcarname),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))),_hresp);
 //BA.debugLineNum = 178;BA.debugLine="Dim hCard As Int = hCarName + hResp + 12dip";
_hcard = (int) (_hcarname+_hresp+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (12)));
 //BA.debugLineNum = 179;BA.debugLine="card.SetLayout(card.Left, card.Top, card.Width";
_card.SetLayout(_card.getLeft(),_card.getTop(),_card.getWidth(),_hcard);
 //BA.debugLineNum = 180;BA.debugLine="y = y + hCard + 8dip";
_y = (int) (_y+_hcard+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)));
 }
};
 } 
       catch (Exception e84) {
			processBA.setLastException(e84); //BA.debugLineNum = 183;BA.debugLine="Log(\"Error leyendo car_reports: \" & LastExcepti";
anywheresoftware.b4a.keywords.Common.LogImpl("78323174","Error leyendo car_reports: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 }else {
 //BA.debugLineNum = 186;BA.debugLine="If rp.ContainsKey(\"raw\") Then";
if (_rp.ContainsKey((Object)("raw"))) { 
 //BA.debugLineNum = 187;BA.debugLine="Dim lblRaw As Label";
_lblraw = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 188;BA.debugLine="lblRaw.Initialize(\"lblRaw\")";
_lblraw.Initialize(mostCurrent.activityBA,"lblRaw");
 //BA.debugLineNum = 189;BA.debugLine="lblRaw.Text = rp.Get(\"raw\")";
_lblraw.setText(BA.ObjectToCharSequence(_rp.Get((Object)("raw"))));
 //BA.debugLineNum = 190;BA.debugLine="lblRaw.TextSize = 13";
_lblraw.setTextSize((float) (13));
 //BA.debugLineNum = 191;BA.debugLine="Dim hRaw As Int = EstimateLabelHeight(lblRaw.Te";
_hraw = _estimatelabelheight(_lblraw.getText(),(int) (_lblraw.getTextSize()),_w);
 //BA.debugLineNum = 192;BA.debugLine="pnl.AddView(lblRaw, padding, y, w, hRaw)";
mostCurrent._pnl.AddView((android.view.View)(_lblraw.getObject()),_padding,_y,_w,_hraw);
 //BA.debugLineNum = 193;BA.debugLine="y = y + hRaw + 6dip";
_y = (int) (_y+_hraw+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 };
 };
 //BA.debugLineNum = 197;BA.debugLine="pnl.Height = y + 20dip";
mostCurrent._pnl.setHeight((int) (_y+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))));
 //BA.debugLineNum = 198;BA.debugLine="End Sub";
return "";
}
}
