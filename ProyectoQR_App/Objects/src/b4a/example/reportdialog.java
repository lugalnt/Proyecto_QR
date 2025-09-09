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

public class reportdialog extends Activity implements B4AActivity{
	public static reportdialog mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.reportdialog");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (reportdialog).");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.reportdialog");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.reportdialog", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (reportdialog) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (reportdialog) Resume **");
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
		return reportdialog.class;
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
            BA.LogInfo("** Activity (reportdialog) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (reportdialog) Pause event (activity is not paused). **");
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
            reportdialog mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (reportdialog) Resume **");
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
public anywheresoftware.b4a.objects.PanelWrapper _pnlmain = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlcontent = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnprev = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnnext = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnsave = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncancel = null;
public anywheresoftware.b4a.objects.collections.Map _areamap = null;
public anywheresoftware.b4a.objects.collections.List _carslist = null;
public static int _currentindex = 0;
public anywheresoftware.b4a.objects.collections.List _curedittexts = null;
public anywheresoftware.b4a.objects.collections.List _curcheckboxes = null;
public anywheresoftware.b4a.objects.collections.List _curproporder = null;
public anywheresoftware.b4a.objects.EditTextWrapper _curobs = null;
public anywheresoftware.b4a.objects.EditTextWrapper _curinc = null;
public anywheresoftware.b4a.objects.collections.List _answerslist = null;
public b4a.example.main _main = null;
public b4a.example.login _login = null;
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
String _raw = "";
int _p = 0;
anywheresoftware.b4a.objects.collections.JSONParser _jp = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
int _i = 0;
String _s = "";
anywheresoftware.b4a.objects.collections.JSONParser _jp2 = null;
anywheresoftware.b4a.objects.collections.Map _m = null;
anywheresoftware.b4a.objects.collections.Map _empty = null;
anywheresoftware.b4a.objects.collections.Map _tmp = null;
 //BA.debugLineNum = 43;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 44;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 47;BA.debugLine="pnlMain.Initialize(\"pnlMain\")";
mostCurrent._pnlmain.Initialize(mostCurrent.activityBA,"pnlMain");
 //BA.debugLineNum = 48;BA.debugLine="Activity.AddView(pnlMain, 0, 0, 100%x, 100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._pnlmain.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 49;BA.debugLine="pnlMain.Color = Colors.White";
mostCurrent._pnlmain.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 51;BA.debugLine="lblTitle.Initialize(\"\")";
mostCurrent._lbltitle.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 52;BA.debugLine="lblTitle.TextSize = 16";
mostCurrent._lbltitle.setTextSize((float) (16));
 //BA.debugLineNum = 53;BA.debugLine="lblTitle.TextColor = Colors.Black";
mostCurrent._lbltitle.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 54;BA.debugLine="pnlMain.AddView(lblTitle, 8dip, 8dip, 84%x, 28dip";
mostCurrent._pnlmain.AddView((android.view.View)(mostCurrent._lbltitle.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (84),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (28)));
 //BA.debugLineNum = 56;BA.debugLine="pnlContent.Initialize(\"pnlContent\")";
mostCurrent._pnlcontent.Initialize(mostCurrent.activityBA,"pnlContent");
 //BA.debugLineNum = 57;BA.debugLine="pnlContent.Color = Colors.White";
mostCurrent._pnlcontent.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 58;BA.debugLine="pnlMain.AddView(pnlContent, 0, 44dip, 100%x, 70%y";
mostCurrent._pnlmain.AddView((android.view.View)(mostCurrent._pnlcontent.getObject()),(int) (0),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (44)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (70),mostCurrent.activityBA));
 //BA.debugLineNum = 60;BA.debugLine="btnPrev.Initialize(\"btnPrev\")";
mostCurrent._btnprev.Initialize(mostCurrent.activityBA,"btnPrev");
 //BA.debugLineNum = 61;BA.debugLine="btnPrev.Text = \"Anterior\"";
mostCurrent._btnprev.setText(BA.ObjectToCharSequence("Anterior"));
 //BA.debugLineNum = 62;BA.debugLine="pnlMain.AddView(btnPrev, 6dip, 76%y, 30%x - 12dip";
mostCurrent._pnlmain.AddView((android.view.View)(mostCurrent._btnprev.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (76),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (12))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (40)));
 //BA.debugLineNum = 64;BA.debugLine="btnNext.Initialize(\"btnNext\")";
mostCurrent._btnnext.Initialize(mostCurrent.activityBA,"btnNext");
 //BA.debugLineNum = 65;BA.debugLine="btnNext.Text = \"Siguiente\"";
mostCurrent._btnnext.setText(BA.ObjectToCharSequence("Siguiente"));
 //BA.debugLineNum = 66;BA.debugLine="pnlMain.AddView(btnNext, 34%x + 6dip, 76%y, 30%x";
mostCurrent._pnlmain.AddView((android.view.View)(mostCurrent._btnnext.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (34),mostCurrent.activityBA)+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6))),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (76),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (12))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (40)));
 //BA.debugLineNum = 68;BA.debugLine="btnSave.Initialize(\"btnSave\")";
mostCurrent._btnsave.Initialize(mostCurrent.activityBA,"btnSave");
 //BA.debugLineNum = 69;BA.debugLine="btnSave.Text = \"Generar JSON\"";
mostCurrent._btnsave.setText(BA.ObjectToCharSequence("Generar JSON"));
 //BA.debugLineNum = 70;BA.debugLine="pnlMain.AddView(btnSave, 62%x + 12dip, 76%y, 36%x";
mostCurrent._pnlmain.AddView((android.view.View)(mostCurrent._btnsave.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (62),mostCurrent.activityBA)+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (12))),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (76),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (36),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (18))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (40)));
 //BA.debugLineNum = 72;BA.debugLine="btnCancel.Initialize(\"btnCancel\")";
mostCurrent._btncancel.Initialize(mostCurrent.activityBA,"btnCancel");
 //BA.debugLineNum = 73;BA.debugLine="btnCancel.Text = \"Cancelar\"";
mostCurrent._btncancel.setText(BA.ObjectToCharSequence("Cancelar"));
 //BA.debugLineNum = 74;BA.debugLine="pnlMain.AddView(btnCancel, 6dip, 82%y + 48dip, 88";
mostCurrent._pnlmain.AddView((android.view.View)(mostCurrent._btncancel.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (82),mostCurrent.activityBA)+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (88),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (36)));
 //BA.debugLineNum = 77;BA.debugLine="If File.Exists(File.DirInternal, \"last_area.json\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"last_area.json")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 78;BA.debugLine="ToastMessageShow(\"No hay datos de área (last_are";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No hay datos de área (last_area.json). Escanea primero."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 79;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 80;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 83;BA.debugLine="Dim raw As String = File.ReadString(File.DirInter";
_raw = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"last_area.json");
 //BA.debugLineNum = 85;BA.debugLine="Dim p As Int = raw.IndexOf(\"{\")";
_p = _raw.indexOf("{");
 //BA.debugLineNum = 86;BA.debugLine="If p > -1 Then raw = raw.SubString(p)";
if (_p>-1) { 
_raw = _raw.substring(_p);};
 //BA.debugLineNum = 88;BA.debugLine="Dim jp As JSONParser";
_jp = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 89;BA.debugLine="jp.Initialize(raw)";
_jp.Initialize(_raw);
 //BA.debugLineNum = 90;BA.debugLine="Dim root As Map";
_root = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 91;BA.debugLine="Try";
try { //BA.debugLineNum = 92;BA.debugLine="root = jp.NextObject";
_root = _jp.NextObject();
 } 
       catch (Exception e38) {
			processBA.setLastException(e38); //BA.debugLineNum = 94;BA.debugLine="ToastMessageShow(\"JSON de área inválido.\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("JSON de área inválido."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 95;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 96;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 99;BA.debugLine="If root.ContainsKey(\"data\") Then";
if (_root.ContainsKey((Object)("data"))) { 
 //BA.debugLineNum = 100;BA.debugLine="areaMap = root.Get(\"data\")";
mostCurrent._areamap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_root.Get((Object)("data"))));
 }else {
 //BA.debugLineNum = 102;BA.debugLine="areaMap = root";
mostCurrent._areamap = _root;
 };
 //BA.debugLineNum = 105;BA.debugLine="If areaMap.IsInitialized = False Then";
if (mostCurrent._areamap.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 106;BA.debugLine="areaMap.Initialize";
mostCurrent._areamap.Initialize();
 };
 //BA.debugLineNum = 109;BA.debugLine="If areaMap.ContainsKey(\"cars\") Then";
if (mostCurrent._areamap.ContainsKey((Object)("cars"))) { 
 //BA.debugLineNum = 110;BA.debugLine="carsList = areaMap.Get(\"cars\")";
mostCurrent._carslist = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(mostCurrent._areamap.Get((Object)("cars"))));
 }else {
 //BA.debugLineNum = 112;BA.debugLine="carsList.Initialize";
mostCurrent._carslist.Initialize();
 };
 //BA.debugLineNum = 115;BA.debugLine="If carsList.IsInitialized = False Or carsList.Siz";
if (mostCurrent._carslist.IsInitialized()==anywheresoftware.b4a.keywords.Common.False || mostCurrent._carslist.getSize()==0) { 
 //BA.debugLineNum = 116;BA.debugLine="ToastMessageShow(\"No se encontraron C.A.R. en el";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se encontraron C.A.R. en el área."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 117;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 118;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 122;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 123;BA.debugLine="For i = 0 To carsList.Size - 1";
{
final int step61 = 1;
final int limit61 = (int) (mostCurrent._carslist.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit61 ;_i = _i + step61 ) {
 //BA.debugLineNum = 124;BA.debugLine="If carsList.Get(i) Is Map Then";
if (mostCurrent._carslist.Get(_i) instanceof java.util.Map) { 
 }else if(mostCurrent._carslist.Get(_i) instanceof String) { 
 //BA.debugLineNum = 127;BA.debugLine="Dim s As String = carsList.Get(i)";
_s = BA.ObjectToString(mostCurrent._carslist.Get(_i));
 //BA.debugLineNum = 128;BA.debugLine="Dim jp2 As JSONParser";
_jp2 = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 129;BA.debugLine="jp2.Initialize(s)";
_jp2.Initialize(_s);
 //BA.debugLineNum = 130;BA.debugLine="Dim m As Map";
_m = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 131;BA.debugLine="Try";
try { //BA.debugLineNum = 132;BA.debugLine="m = jp2.NextObject";
_m = _jp2.NextObject();
 } 
       catch (Exception e71) {
			processBA.setLastException(e71); //BA.debugLineNum = 134;BA.debugLine="m.Initialize";
_m.Initialize();
 };
 //BA.debugLineNum = 136;BA.debugLine="carsList.Set(i, m)";
mostCurrent._carslist.Set(_i,(Object)(_m.getObject()));
 }else {
 //BA.debugLineNum = 138;BA.debugLine="Dim empty As Map";
_empty = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 139;BA.debugLine="empty.Initialize";
_empty.Initialize();
 //BA.debugLineNum = 140;BA.debugLine="carsList.Set(i, empty)";
mostCurrent._carslist.Set(_i,(Object)(_empty.getObject()));
 };
 }
};
 //BA.debugLineNum = 145;BA.debugLine="answersList.Initialize";
mostCurrent._answerslist.Initialize();
 //BA.debugLineNum = 146;BA.debugLine="For i = 0 To carsList.Size - 1";
{
final int step81 = 1;
final int limit81 = (int) (mostCurrent._carslist.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit81 ;_i = _i + step81 ) {
 //BA.debugLineNum = 147;BA.debugLine="Dim tmp As Map";
_tmp = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 148;BA.debugLine="tmp.Initialize";
_tmp.Initialize();
 //BA.debugLineNum = 149;BA.debugLine="answersList.Add(tmp)";
mostCurrent._answerslist.Add((Object)(_tmp.getObject()));
 }
};
 //BA.debugLineNum = 153;BA.debugLine="curEditTexts.Initialize";
mostCurrent._curedittexts.Initialize();
 //BA.debugLineNum = 154;BA.debugLine="curCheckBoxes.Initialize";
mostCurrent._curcheckboxes.Initialize();
 //BA.debugLineNum = 155;BA.debugLine="curPropOrder.Initialize";
mostCurrent._curproporder.Initialize();
 //BA.debugLineNum = 156;BA.debugLine="currentIndex = 0";
_currentindex = (int) (0);
 //BA.debugLineNum = 157;BA.debugLine="ShowCAR(currentIndex, True)";
_showcar(_currentindex,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 158;BA.debugLine="End Sub";
return "";
}
public static String  _btncancel_click() throws Exception{
 //BA.debugLineNum = 405;BA.debugLine="Private Sub btnCancel_Click";
 //BA.debugLineNum = 406;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 407;BA.debugLine="End Sub";
return "";
}
public static String  _btnnext_click() throws Exception{
 //BA.debugLineNum = 399;BA.debugLine="Private Sub btnNext_Click";
 //BA.debugLineNum = 400;BA.debugLine="If currentIndex < carsList.Size - 1 Then";
if (_currentindex<mostCurrent._carslist.getSize()-1) { 
 //BA.debugLineNum = 401;BA.debugLine="ShowCAR(currentIndex + 1, False)";
_showcar((int) (_currentindex+1),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 403;BA.debugLine="End Sub";
return "";
}
public static String  _btnprev_click() throws Exception{
 //BA.debugLineNum = 393;BA.debugLine="Private Sub btnPrev_Click";
 //BA.debugLineNum = 394;BA.debugLine="If currentIndex > 0 Then";
if (_currentindex>0) { 
 //BA.debugLineNum = 395;BA.debugLine="ShowCAR(currentIndex - 1, False)";
_showcar((int) (_currentindex-1),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 397;BA.debugLine="End Sub";
return "";
}
public static String  _btnsave_click() throws Exception{
anywheresoftware.b4a.objects.collections.Map _report = null;
anywheresoftware.b4a.objects.collections.Map _areasummary = null;
anywheresoftware.b4a.objects.collections.List _carreports = null;
int _i = 0;
anywheresoftware.b4a.objects.collections.Map _m = null;
anywheresoftware.b4a.objects.collections.Map _fallback = null;
anywheresoftware.b4a.objects.collections.Map _carmap = null;
anywheresoftware.b4a.objects.collections.Map _fallback2 = null;
anywheresoftware.b4a.objects.collections.Map _carmap2 = null;
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _jg = null;
String _reportjson = "";
 //BA.debugLineNum = 409;BA.debugLine="Private Sub btnSave_Click";
 //BA.debugLineNum = 411;BA.debugLine="SaveCurrentValues";
_savecurrentvalues();
 //BA.debugLineNum = 413;BA.debugLine="Dim report As Map";
_report = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 414;BA.debugLine="report.Initialize";
_report.Initialize();
 //BA.debugLineNum = 416;BA.debugLine="Dim areaSummary As Map";
_areasummary = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 417;BA.debugLine="areaSummary.Initialize";
_areasummary.Initialize();
 //BA.debugLineNum = 418;BA.debugLine="areaSummary.Put(\"area_name\", GetFirstString(areaM";
_areasummary.Put((Object)("area_name"),(Object)(_getfirststring(mostCurrent._areamap,new String[]{"area_name","Nombre_Area","name"})));
 //BA.debugLineNum = 419;BA.debugLine="areaSummary.Put(\"area_description\", GetFirstStrin";
_areasummary.Put((Object)("area_description"),(Object)(_getfirststring(mostCurrent._areamap,new String[]{"area_description","Descripcion_Area","description"})));
 //BA.debugLineNum = 420;BA.debugLine="report.Put(\"area\", areaSummary)";
_report.Put((Object)("area"),(Object)(_areasummary.getObject()));
 //BA.debugLineNum = 422;BA.debugLine="Dim carReports As List";
_carreports = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 423;BA.debugLine="carReports.Initialize";
_carreports.Initialize();
 //BA.debugLineNum = 425;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 426;BA.debugLine="For i = 0 To answersList.Size - 1";
{
final int step12 = 1;
final int limit12 = (int) (mostCurrent._answerslist.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit12 ;_i = _i + step12 ) {
 //BA.debugLineNum = 427;BA.debugLine="If answersList.Get(i) Is Map Then";
if (mostCurrent._answerslist.Get(_i) instanceof java.util.Map) { 
 //BA.debugLineNum = 428;BA.debugLine="Dim m As Map = answersList.Get(i)";
_m = new anywheresoftware.b4a.objects.collections.Map();
_m = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._answerslist.Get(_i)));
 //BA.debugLineNum = 429;BA.debugLine="If m.IsInitialized And m.Size > 0 Then";
if (_m.IsInitialized() && _m.getSize()>0) { 
 //BA.debugLineNum = 430;BA.debugLine="carReports.Add(m)";
_carreports.Add((Object)(_m.getObject()));
 }else {
 //BA.debugLineNum = 433;BA.debugLine="Dim fallback As Map";
_fallback = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 434;BA.debugLine="fallback.Initialize";
_fallback.Initialize();
 //BA.debugLineNum = 435;BA.debugLine="Dim carMap As Map = carsList.Get(i)";
_carmap = new anywheresoftware.b4a.objects.collections.Map();
_carmap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._carslist.Get(_i)));
 //BA.debugLineNum = 436;BA.debugLine="fallback.Put(\"car_name\", GetFirstString(carMap";
_fallback.Put((Object)("car_name"),(Object)(_getfirststring(_carmap,new String[]{"name","nombre","Nombre"})));
 //BA.debugLineNum = 437;BA.debugLine="fallback.Put(\"responses\", CreateMap())";
_fallback.Put((Object)("responses"),(Object)(anywheresoftware.b4a.keywords.Common.createMap(new Object[] {}).getObject()));
 //BA.debugLineNum = 438;BA.debugLine="carReports.Add(fallback)";
_carreports.Add((Object)(_fallback.getObject()));
 };
 }else {
 //BA.debugLineNum = 441;BA.debugLine="Dim fallback2 As Map";
_fallback2 = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 442;BA.debugLine="fallback2.Initialize";
_fallback2.Initialize();
 //BA.debugLineNum = 443;BA.debugLine="Dim carMap2 As Map = carsList.Get(i)";
_carmap2 = new anywheresoftware.b4a.objects.collections.Map();
_carmap2 = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._carslist.Get(_i)));
 //BA.debugLineNum = 444;BA.debugLine="fallback2.Put(\"car_name\", GetFirstString(carMap";
_fallback2.Put((Object)("car_name"),(Object)(_getfirststring(_carmap2,new String[]{"name","nombre","Nombre"})));
 //BA.debugLineNum = 445;BA.debugLine="fallback2.Put(\"responses\", CreateMap())";
_fallback2.Put((Object)("responses"),(Object)(anywheresoftware.b4a.keywords.Common.createMap(new Object[] {}).getObject()));
 //BA.debugLineNum = 446;BA.debugLine="carReports.Add(fallback2)";
_carreports.Add((Object)(_fallback2.getObject()));
 };
 }
};
 //BA.debugLineNum = 450;BA.debugLine="report.Put(\"car_reports\", carReports)";
_report.Put((Object)("car_reports"),(Object)(_carreports.getObject()));
 //BA.debugLineNum = 452;BA.debugLine="Dim jg As JSONGenerator";
_jg = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 453;BA.debugLine="jg.Initialize(report)";
_jg.Initialize(_report);
 //BA.debugLineNum = 454;BA.debugLine="Dim reportJson As String = jg.ToString";
_reportjson = _jg.ToString();
 //BA.debugLineNum = 456;BA.debugLine="File.WriteString(File.DirInternal, \"report.json\",";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"report.json",_reportjson);
 //BA.debugLineNum = 457;BA.debugLine="MsgboxAsync(\"Reporte generado y guardado en repor";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Reporte generado y guardado en report.json"),BA.ObjectToCharSequence("OK"),processBA);
 //BA.debugLineNum = 458;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 459;BA.debugLine="End Sub";
return "";
}
public static String  _getfirststring(anywheresoftware.b4a.objects.collections.Map _data,String[] _keys) throws Exception{
int _i = 0;
String _k = "";
 //BA.debugLineNum = 462;BA.debugLine="Private Sub GetFirstString(data As Map, keys() As";
 //BA.debugLineNum = 463;BA.debugLine="If data.IsInitialized = False Then Return \"\"";
if (_data.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
if (true) return "";};
 //BA.debugLineNum = 464;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 465;BA.debugLine="For i = 0 To keys.Length - 1";
{
final int step3 = 1;
final int limit3 = (int) (_keys.length-1);
_i = (int) (0) ;
for (;_i <= limit3 ;_i = _i + step3 ) {
 //BA.debugLineNum = 466;BA.debugLine="Dim k As String = keys(i)";
_k = _keys[_i];
 //BA.debugLineNum = 467;BA.debugLine="If data.ContainsKey(k) Then";
if (_data.ContainsKey((Object)(_k))) { 
 //BA.debugLineNum = 468;BA.debugLine="If data.Get(k) <> Null Then";
if (_data.Get((Object)(_k))!= null) { 
 //BA.debugLineNum = 469;BA.debugLine="Return data.Get(k)";
if (true) return BA.ObjectToString(_data.Get((Object)(_k)));
 };
 };
 }
};
 //BA.debugLineNum = 473;BA.debugLine="Return \"\"";
if (true) return "";
 //BA.debugLineNum = 474;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 18;BA.debugLine="Private pnlMain As Panel";
mostCurrent._pnlmain = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private lblTitle As Label";
mostCurrent._lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private pnlContent As Panel";
mostCurrent._pnlcontent = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private btnPrev As Button";
mostCurrent._btnprev = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private btnNext As Button";
mostCurrent._btnnext = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private btnSave As Button";
mostCurrent._btnsave = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private btnCancel As Button";
mostCurrent._btncancel = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private areaMap As Map";
mostCurrent._areamap = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 28;BA.debugLine="Private carsList As List          ' Lista de CARs";
mostCurrent._carslist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 29;BA.debugLine="Private currentIndex As Int       ' índice del CA";
_currentindex = 0;
 //BA.debugLineNum = 32;BA.debugLine="Private curEditTexts As List      ' List<EditText";
mostCurrent._curedittexts = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 33;BA.debugLine="Private curCheckBoxes As List     ' List<CheckBox";
mostCurrent._curcheckboxes = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 34;BA.debugLine="Private curPropOrder As List      ' List<Map{type";
mostCurrent._curproporder = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 35;BA.debugLine="Private curObs As EditText        ' observación d";
mostCurrent._curobs = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private curInc As EditText        ' incidencia de";
mostCurrent._curinc = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private answersList As List";
mostCurrent._answerslist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public static String  _savecurrentvalues() throws Exception{
anywheresoftware.b4a.objects.collections.Map _responses = null;
int _k = 0;
anywheresoftware.b4a.objects.collections.Map _meta = null;
String _t = "";
String _label = "";
int _idx = 0;
anywheresoftware.b4a.objects.EditTextWrapper _et = null;
String _v = "";
anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _cb = null;
anywheresoftware.b4a.objects.collections.Map _store = null;
anywheresoftware.b4a.objects.collections.Map _carmap = null;
 //BA.debugLineNum = 357;BA.debugLine="Private Sub SaveCurrentValues";
 //BA.debugLineNum = 358;BA.debugLine="If curPropOrder.IsInitialized = False Then Return";
if (mostCurrent._curproporder.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
if (true) return "";};
 //BA.debugLineNum = 360;BA.debugLine="Dim responses As Map";
_responses = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 361;BA.debugLine="responses.Initialize";
_responses.Initialize();
 //BA.debugLineNum = 363;BA.debugLine="Dim k As Int";
_k = 0;
 //BA.debugLineNum = 364;BA.debugLine="For k = 0 To curPropOrder.Size - 1";
{
final int step5 = 1;
final int limit5 = (int) (mostCurrent._curproporder.getSize()-1);
_k = (int) (0) ;
for (;_k <= limit5 ;_k = _k + step5 ) {
 //BA.debugLineNum = 365;BA.debugLine="Dim meta As Map = curPropOrder.Get(k)";
_meta = new anywheresoftware.b4a.objects.collections.Map();
_meta = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._curproporder.Get(_k)));
 //BA.debugLineNum = 366;BA.debugLine="Dim t As String = meta.Get(\"type\")";
_t = BA.ObjectToString(_meta.Get((Object)("type")));
 //BA.debugLineNum = 367;BA.debugLine="Dim label As String = meta.Get(\"label\")";
_label = BA.ObjectToString(_meta.Get((Object)("label")));
 //BA.debugLineNum = 368;BA.debugLine="Dim idx As Int = meta.Get(\"idx\")";
_idx = (int)(BA.ObjectToNumber(_meta.Get((Object)("idx"))));
 //BA.debugLineNum = 370;BA.debugLine="If t = \"edittext\" Then";
if ((_t).equals("edittext")) { 
 //BA.debugLineNum = 371;BA.debugLine="Dim et As EditText = curEditTexts.Get(idx)";
_et = new anywheresoftware.b4a.objects.EditTextWrapper();
_et = (anywheresoftware.b4a.objects.EditTextWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.EditTextWrapper(), (android.widget.EditText)(mostCurrent._curedittexts.Get(_idx)));
 //BA.debugLineNum = 372;BA.debugLine="Dim v As String = et.Text";
_v = _et.getText();
 //BA.debugLineNum = 373;BA.debugLine="If v <> \"\" Then";
if ((_v).equals("") == false) { 
 //BA.debugLineNum = 374;BA.debugLine="responses.Put(label, v)";
_responses.Put((Object)(_label),(Object)(_v));
 };
 }else if((_t).equals("checkbox")) { 
 //BA.debugLineNum = 377;BA.debugLine="Dim cb As CheckBox = curCheckBoxes.Get(idx)";
_cb = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
_cb = (anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper(), (android.widget.CheckBox)(mostCurrent._curcheckboxes.Get(_idx)));
 //BA.debugLineNum = 378;BA.debugLine="responses.Put(label, cb.Checked)";
_responses.Put((Object)(_label),(Object)(_cb.getChecked()));
 };
 }
};
 //BA.debugLineNum = 382;BA.debugLine="Dim store As Map";
_store = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 383;BA.debugLine="store.Initialize";
_store.Initialize();
 //BA.debugLineNum = 384;BA.debugLine="Dim carMap As Map = carsList.Get(currentIndex)";
_carmap = new anywheresoftware.b4a.objects.collections.Map();
_carmap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._carslist.Get(_currentindex)));
 //BA.debugLineNum = 385;BA.debugLine="store.Put(\"car_name\", GetFirstString(carMap, Arra";
_store.Put((Object)("car_name"),(Object)(_getfirststring(_carmap,new String[]{"name","nombre","Nombre"})));
 //BA.debugLineNum = 386;BA.debugLine="store.Put(\"responses\", responses)";
_store.Put((Object)("responses"),(Object)(_responses.getObject()));
 //BA.debugLineNum = 387;BA.debugLine="If curObs.IsInitialized And curObs.Text <> \"\" The";
if (mostCurrent._curobs.IsInitialized() && (mostCurrent._curobs.getText()).equals("") == false) { 
_store.Put((Object)("observacion"),(Object)(mostCurrent._curobs.getText()));};
 //BA.debugLineNum = 388;BA.debugLine="If curInc.IsInitialized And curInc.Text <> \"\" The";
if (mostCurrent._curinc.IsInitialized() && (mostCurrent._curinc.getText()).equals("") == false) { 
_store.Put((Object)("incidencia"),(Object)(mostCurrent._curinc.getText()));};
 //BA.debugLineNum = 390;BA.debugLine="answersList.Set(currentIndex, store)";
mostCurrent._answerslist.Set(_currentindex,(Object)(_store.getObject()));
 //BA.debugLineNum = 391;BA.debugLine="End Sub";
return "";
}
public static String  _showcar(int _index,boolean _isfirst) throws Exception{
anywheresoftware.b4a.objects.collections.Map _carmap = null;
String _carname = "";
anywheresoftware.b4a.objects.collections.Map _saved = null;
anywheresoftware.b4a.objects.collections.Map _savedresp = null;
String _savedobs = "";
String _savedinc = "";
int _top = 0;
anywheresoftware.b4a.objects.collections.List _props = null;
int _j = 0;
anywheresoftware.b4a.objects.collections.Map _prop = null;
anywheresoftware.b4a.objects.collections.Map _mp = null;
String _label = "";
String _ptype = "";
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _cb = null;
boolean _dflt = false;
anywheresoftware.b4a.objects.collections.Map _metacb = null;
anywheresoftware.b4a.objects.EditTextWrapper _etn = null;
String _hint = "";
String _txt = "";
anywheresoftware.b4a.objects.collections.Map _metan = null;
anywheresoftware.b4a.objects.EditTextWrapper _ett = null;
String _placeholder = "";
String _txt2 = "";
anywheresoftware.b4a.objects.collections.Map _metat = null;
anywheresoftware.b4a.objects.LabelWrapper _lblobs = null;
anywheresoftware.b4a.objects.LabelWrapper _lblinc = null;
 //BA.debugLineNum = 161;BA.debugLine="Private Sub ShowCAR(index As Int, isFirst As Boole";
 //BA.debugLineNum = 162;BA.debugLine="If isFirst = False Then";
if (_isfirst==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 163;BA.debugLine="SaveCurrentValues";
_savecurrentvalues();
 };
 //BA.debugLineNum = 166;BA.debugLine="pnlContent.RemoveAllViews";
mostCurrent._pnlcontent.RemoveAllViews();
 //BA.debugLineNum = 167;BA.debugLine="curEditTexts.Initialize";
mostCurrent._curedittexts.Initialize();
 //BA.debugLineNum = 168;BA.debugLine="curCheckBoxes.Initialize";
mostCurrent._curcheckboxes.Initialize();
 //BA.debugLineNum = 169;BA.debugLine="curPropOrder.Initialize";
mostCurrent._curproporder.Initialize();
 //BA.debugLineNum = 171;BA.debugLine="If index < 0 Or index > carsList.Size - 1 Then Re";
if (_index<0 || _index>mostCurrent._carslist.getSize()-1) { 
if (true) return "";};
 //BA.debugLineNum = 173;BA.debugLine="Dim carMap As Map = carsList.Get(index)";
_carmap = new anywheresoftware.b4a.objects.collections.Map();
_carmap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._carslist.Get(_index)));
 //BA.debugLineNum = 174;BA.debugLine="Dim carName As String = GetFirstString(carMap, Ar";
_carname = _getfirststring(_carmap,new String[]{"name","nombre","Nombre"});
 //BA.debugLineNum = 175;BA.debugLine="If carName = \"\" Then carName = \"C.A.R. \" & (index";
if ((_carname).equals("")) { 
_carname = "C.A.R. "+BA.NumberToString((_index+1));};
 //BA.debugLineNum = 176;BA.debugLine="lblTitle.Text = \"Reportar: \" & carName & \"  (\" &";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence("Reportar: "+_carname+"  ("+BA.NumberToString((_index+1))+" de "+BA.NumberToString(mostCurrent._carslist.getSize())+")"));
 //BA.debugLineNum = 179;BA.debugLine="Dim saved As Map = answersList.Get(index)";
_saved = new anywheresoftware.b4a.objects.collections.Map();
_saved = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(mostCurrent._answerslist.Get(_index)));
 //BA.debugLineNum = 180;BA.debugLine="Dim savedResp As Map";
_savedresp = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 181;BA.debugLine="savedResp.Initialize";
_savedresp.Initialize();
 //BA.debugLineNum = 182;BA.debugLine="If saved.IsInitialized And saved.ContainsKey(\"res";
if (_saved.IsInitialized() && _saved.ContainsKey((Object)("responses"))) { 
 //BA.debugLineNum = 183;BA.debugLine="savedResp = saved.Get(\"responses\")";
_savedresp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_saved.Get((Object)("responses"))));
 };
 //BA.debugLineNum = 185;BA.debugLine="Dim savedObs As String = \"\"";
_savedobs = "";
 //BA.debugLineNum = 186;BA.debugLine="Dim savedInc As String = \"\"";
_savedinc = "";
 //BA.debugLineNum = 187;BA.debugLine="If saved.IsInitialized And saved.ContainsKey(\"obs";
if (_saved.IsInitialized() && _saved.ContainsKey((Object)("observacion"))) { 
_savedobs = BA.ObjectToString(_saved.Get((Object)("observacion")));};
 //BA.debugLineNum = 188;BA.debugLine="If saved.IsInitialized And saved.ContainsKey(\"inc";
if (_saved.IsInitialized() && _saved.ContainsKey((Object)("incidencia"))) { 
_savedinc = BA.ObjectToString(_saved.Get((Object)("incidencia")));};
 //BA.debugLineNum = 191;BA.debugLine="Dim top As Int = 8dip";
_top = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8));
 //BA.debugLineNum = 192;BA.debugLine="Dim props As List";
_props = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 193;BA.debugLine="If carMap.ContainsKey(\"properties\") Then";
if (_carmap.ContainsKey((Object)("properties"))) { 
 //BA.debugLineNum = 194;BA.debugLine="props = carMap.Get(\"properties\")";
_props = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_carmap.Get((Object)("properties"))));
 }else {
 //BA.debugLineNum = 196;BA.debugLine="props.Initialize";
_props.Initialize();
 };
 //BA.debugLineNum = 199;BA.debugLine="Dim j As Int";
_j = 0;
 //BA.debugLineNum = 200;BA.debugLine="For j = 0 To props.Size - 1";
{
final int step31 = 1;
final int limit31 = (int) (_props.getSize()-1);
_j = (int) (0) ;
for (;_j <= limit31 ;_j = _j + step31 ) {
 //BA.debugLineNum = 201;BA.debugLine="Dim prop As Map";
_prop = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 202;BA.debugLine="If props.Get(j) Is Map Then";
if (_props.Get(_j) instanceof java.util.Map) { 
 //BA.debugLineNum = 203;BA.debugLine="prop = props.Get(j)";
_prop = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_props.Get(_j)));
 }else {
 //BA.debugLineNum = 205;BA.debugLine="Dim mp As Map";
_mp = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 206;BA.debugLine="mp.Initialize";
_mp.Initialize();
 //BA.debugLineNum = 207;BA.debugLine="prop = mp";
_prop = _mp;
 };
 //BA.debugLineNum = 210;BA.debugLine="Dim label As String = GetFirstString(prop, Array";
_label = _getfirststring(_prop,new String[]{"label","name","key"});
 //BA.debugLineNum = 211;BA.debugLine="If label = \"\" Then label = \"prop_\" & j";
if ((_label).equals("")) { 
_label = "prop_"+BA.NumberToString(_j);};
 //BA.debugLineNum = 213;BA.debugLine="Dim ptype As String = GetFirstString(prop, Array";
_ptype = _getfirststring(_prop,new String[]{"type","tipo"}).toLowerCase();
 //BA.debugLineNum = 216;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 217;BA.debugLine="lbl.Initialize(\"\")";
_lbl.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 218;BA.debugLine="lbl.Text = label";
_lbl.setText(BA.ObjectToCharSequence(_label));
 //BA.debugLineNum = 219;BA.debugLine="lbl.TextSize = 12";
_lbl.setTextSize((float) (12));
 //BA.debugLineNum = 220;BA.debugLine="lbl.TextColor = Colors.Black";
_lbl.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 221;BA.debugLine="pnlContent.AddView(lbl, 8dip, top, 84%x, 22dip)";
mostCurrent._pnlcontent.AddView((android.view.View)(_lbl.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),_top,anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (84),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (22)));
 //BA.debugLineNum = 223;BA.debugLine="If ptype = \"bool\" Or ptype = \"boolean\" Then";
if ((_ptype).equals("bool") || (_ptype).equals("boolean")) { 
 //BA.debugLineNum = 224;BA.debugLine="Dim cb As CheckBox";
_cb = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 225;BA.debugLine="cb.Initialize(\"\")";
_cb.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 226;BA.debugLine="Dim dflt As Boolean = False";
_dflt = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 227;BA.debugLine="If prop.ContainsKey(\"default\") Then";
if (_prop.ContainsKey((Object)("default"))) { 
 //BA.debugLineNum = 228;BA.debugLine="Try";
try { //BA.debugLineNum = 229;BA.debugLine="dflt = prop.Get(\"default\")";
_dflt = BA.ObjectToBoolean(_prop.Get((Object)("default")));
 } 
       catch (Exception e57) {
			processBA.setLastException(e57); //BA.debugLineNum = 231;BA.debugLine="dflt = False";
_dflt = anywheresoftware.b4a.keywords.Common.False;
 };
 };
 //BA.debugLineNum = 235;BA.debugLine="If savedResp.ContainsKey(label) Then";
if (_savedresp.ContainsKey((Object)(_label))) { 
 //BA.debugLineNum = 236;BA.debugLine="Try";
try { //BA.debugLineNum = 237;BA.debugLine="dflt = savedResp.Get(label)";
_dflt = BA.ObjectToBoolean(_savedresp.Get((Object)(_label)));
 } 
       catch (Exception e64) {
			processBA.setLastException(e64); };
 };
 //BA.debugLineNum = 241;BA.debugLine="cb.Checked = dflt";
_cb.setChecked(_dflt);
 //BA.debugLineNum = 242;BA.debugLine="pnlContent.AddView(cb, 8dip, top + 24dip, 24dip";
mostCurrent._pnlcontent.AddView((android.view.View)(_cb.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),(int) (_top+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (24))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (24)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (24)));
 //BA.debugLineNum = 244;BA.debugLine="curCheckBoxes.Add(cb)";
mostCurrent._curcheckboxes.Add((Object)(_cb.getObject()));
 //BA.debugLineNum = 245;BA.debugLine="Dim metaCb As Map";
_metacb = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 246;BA.debugLine="metaCb.Initialize";
_metacb.Initialize();
 //BA.debugLineNum = 247;BA.debugLine="metaCb.Put(\"type\", \"checkbox\")";
_metacb.Put((Object)("type"),(Object)("checkbox"));
 //BA.debugLineNum = 248;BA.debugLine="metaCb.Put(\"label\", label)";
_metacb.Put((Object)("label"),(Object)(_label));
 //BA.debugLineNum = 249;BA.debugLine="metaCb.Put(\"idx\", curCheckBoxes.Size - 1)";
_metacb.Put((Object)("idx"),(Object)(mostCurrent._curcheckboxes.getSize()-1));
 //BA.debugLineNum = 250;BA.debugLine="curPropOrder.Add(metaCb)";
mostCurrent._curproporder.Add((Object)(_metacb.getObject()));
 //BA.debugLineNum = 252;BA.debugLine="top = top + 24dip + 16dip";
_top = (int) (_top+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (24))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16)));
 }else if((_ptype).equals("number") || (_ptype).equals("integer") || (_ptype).equals("float")) { 
 //BA.debugLineNum = 255;BA.debugLine="Dim etN As EditText";
_etn = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 256;BA.debugLine="etN.Initialize(\"\")";
_etn.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 257;BA.debugLine="etN.SingleLine = True";
_etn.setSingleLine(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 258;BA.debugLine="Dim hint As String = \"\"";
_hint = "";
 //BA.debugLineNum = 259;BA.debugLine="If prop.ContainsKey(\"min\") Then hint = \"min:\" &";
if (_prop.ContainsKey((Object)("min"))) { 
_hint = "min:"+BA.ObjectToString(_prop.Get((Object)("min")));};
 //BA.debugLineNum = 260;BA.debugLine="If prop.ContainsKey(\"max\") Then";
if (_prop.ContainsKey((Object)("max"))) { 
 //BA.debugLineNum = 261;BA.debugLine="If hint.Length > 0 Then hint = hint & \" \"";
if (_hint.length()>0) { 
_hint = _hint+" ";};
 //BA.debugLineNum = 262;BA.debugLine="hint = hint & \"max:\" & prop.Get(\"max\")";
_hint = _hint+"max:"+BA.ObjectToString(_prop.Get((Object)("max")));
 };
 //BA.debugLineNum = 264;BA.debugLine="If prop.ContainsKey(\"step\") Then";
if (_prop.ContainsKey((Object)("step"))) { 
 //BA.debugLineNum = 265;BA.debugLine="If hint.Length > 0 Then hint = hint & \" \"";
if (_hint.length()>0) { 
_hint = _hint+" ";};
 //BA.debugLineNum = 266;BA.debugLine="hint = hint & \"step:\" & prop.Get(\"step\")";
_hint = _hint+"step:"+BA.ObjectToString(_prop.Get((Object)("step")));
 };
 //BA.debugLineNum = 268;BA.debugLine="etN.Hint = hint";
_etn.setHint(_hint);
 //BA.debugLineNum = 270;BA.debugLine="Dim txt As String = \"\"";
_txt = "";
 //BA.debugLineNum = 271;BA.debugLine="If prop.ContainsKey(\"default\") Then txt = prop.";
if (_prop.ContainsKey((Object)("default"))) { 
_txt = BA.ObjectToString(_prop.Get((Object)("default")));};
 //BA.debugLineNum = 272;BA.debugLine="If savedResp.ContainsKey(label) Then";
if (_savedresp.ContainsKey((Object)(_label))) { 
 //BA.debugLineNum = 273;BA.debugLine="Try";
try { //BA.debugLineNum = 274;BA.debugLine="txt = savedResp.Get(label)";
_txt = BA.ObjectToString(_savedresp.Get((Object)(_label)));
 } 
       catch (Exception e97) {
			processBA.setLastException(e97); };
 };
 //BA.debugLineNum = 278;BA.debugLine="etN.Text = txt";
_etn.setText(BA.ObjectToCharSequence(_txt));
 //BA.debugLineNum = 280;BA.debugLine="pnlContent.AddView(etN, 8dip, top + 22dip, 84%x";
mostCurrent._pnlcontent.AddView((android.view.View)(_etn.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),(int) (_top+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (22))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (84),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (34)));
 //BA.debugLineNum = 282;BA.debugLine="curEditTexts.Add(etN)";
mostCurrent._curedittexts.Add((Object)(_etn.getObject()));
 //BA.debugLineNum = 283;BA.debugLine="Dim metaN As Map";
_metan = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 284;BA.debugLine="metaN.Initialize";
_metan.Initialize();
 //BA.debugLineNum = 285;BA.debugLine="metaN.Put(\"type\", \"edittext\")";
_metan.Put((Object)("type"),(Object)("edittext"));
 //BA.debugLineNum = 286;BA.debugLine="metaN.Put(\"label\", label)";
_metan.Put((Object)("label"),(Object)(_label));
 //BA.debugLineNum = 287;BA.debugLine="metaN.Put(\"idx\", curEditTexts.Size - 1)";
_metan.Put((Object)("idx"),(Object)(mostCurrent._curedittexts.getSize()-1));
 //BA.debugLineNum = 288;BA.debugLine="curPropOrder.Add(metaN)";
mostCurrent._curproporder.Add((Object)(_metan.getObject()));
 //BA.debugLineNum = 290;BA.debugLine="top = top + 22dip + 34dip + 8dip";
_top = (int) (_top+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (22))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (34))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)));
 }else {
 //BA.debugLineNum = 294;BA.debugLine="Dim etT As EditText";
_ett = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 295;BA.debugLine="etT.Initialize(\"\")";
_ett.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 296;BA.debugLine="Dim placeholder As String = \"\"";
_placeholder = "";
 //BA.debugLineNum = 297;BA.debugLine="If prop.ContainsKey(\"placeholder\") Then placeho";
if (_prop.ContainsKey((Object)("placeholder"))) { 
_placeholder = BA.ObjectToString(_prop.Get((Object)("placeholder")));};
 //BA.debugLineNum = 298;BA.debugLine="etT.Hint = placeholder";
_ett.setHint(_placeholder);
 //BA.debugLineNum = 299;BA.debugLine="etT.SingleLine = False";
_ett.setSingleLine(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 301;BA.debugLine="Dim txt2 As String = \"\"";
_txt2 = "";
 //BA.debugLineNum = 302;BA.debugLine="If prop.ContainsKey(\"default\") Then txt2 = prop";
if (_prop.ContainsKey((Object)("default"))) { 
_txt2 = BA.ObjectToString(_prop.Get((Object)("default")));};
 //BA.debugLineNum = 303;BA.debugLine="If savedResp.ContainsKey(label) Then";
if (_savedresp.ContainsKey((Object)(_label))) { 
 //BA.debugLineNum = 304;BA.debugLine="Try";
try { //BA.debugLineNum = 305;BA.debugLine="txt2 = savedResp.Get(label)";
_txt2 = BA.ObjectToString(_savedresp.Get((Object)(_label)));
 } 
       catch (Exception e122) {
			processBA.setLastException(e122); };
 };
 //BA.debugLineNum = 309;BA.debugLine="etT.Text = txt2";
_ett.setText(BA.ObjectToCharSequence(_txt2));
 //BA.debugLineNum = 311;BA.debugLine="pnlContent.AddView(etT, 8dip, top + 22dip, 84%x";
mostCurrent._pnlcontent.AddView((android.view.View)(_ett.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),(int) (_top+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (22))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (84),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (72)));
 //BA.debugLineNum = 313;BA.debugLine="curEditTexts.Add(etT)";
mostCurrent._curedittexts.Add((Object)(_ett.getObject()));
 //BA.debugLineNum = 314;BA.debugLine="Dim metaT As Map";
_metat = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 315;BA.debugLine="metaT.Initialize";
_metat.Initialize();
 //BA.debugLineNum = 316;BA.debugLine="metaT.Put(\"type\", \"edittext\")";
_metat.Put((Object)("type"),(Object)("edittext"));
 //BA.debugLineNum = 317;BA.debugLine="metaT.Put(\"label\", label)";
_metat.Put((Object)("label"),(Object)(_label));
 //BA.debugLineNum = 318;BA.debugLine="metaT.Put(\"idx\", curEditTexts.Size - 1)";
_metat.Put((Object)("idx"),(Object)(mostCurrent._curedittexts.getSize()-1));
 //BA.debugLineNum = 319;BA.debugLine="curPropOrder.Add(metaT)";
mostCurrent._curproporder.Add((Object)(_metat.getObject()));
 //BA.debugLineNum = 321;BA.debugLine="top = top + 22dip + 72dip + 8dip";
_top = (int) (_top+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (22))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (72))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)));
 };
 }
};
 //BA.debugLineNum = 326;BA.debugLine="Dim lblObs As Label";
_lblobs = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 327;BA.debugLine="lblObs.Initialize(\"\")";
_lblobs.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 328;BA.debugLine="lblObs.Text = \"Observación\"";
_lblobs.setText(BA.ObjectToCharSequence("Observación"));
 //BA.debugLineNum = 329;BA.debugLine="lblObs.TextSize = 12";
_lblobs.setTextSize((float) (12));
 //BA.debugLineNum = 330;BA.debugLine="pnlContent.AddView(lblObs, 8dip, top, 84%x, 20dip";
mostCurrent._pnlcontent.AddView((android.view.View)(_lblobs.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),_top,anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (84),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)));
 //BA.debugLineNum = 332;BA.debugLine="curObs.Initialize(\"\")";
mostCurrent._curobs.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 333;BA.debugLine="curObs.SingleLine = False";
mostCurrent._curobs.setSingleLine(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 334;BA.debugLine="curObs.Text = savedObs";
mostCurrent._curobs.setText(BA.ObjectToCharSequence(_savedobs));
 //BA.debugLineNum = 335;BA.debugLine="pnlContent.AddView(curObs, 8dip, top + 20dip, 84%";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._curobs.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),(int) (_top+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (84),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (70)));
 //BA.debugLineNum = 336;BA.debugLine="top = top + 20dip + 70dip + 8dip";
_top = (int) (_top+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (70))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)));
 //BA.debugLineNum = 339;BA.debugLine="Dim lblInc As Label";
_lblinc = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 340;BA.debugLine="lblInc.Initialize(\"\")";
_lblinc.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 341;BA.debugLine="lblInc.Text = \"Incidencia\"";
_lblinc.setText(BA.ObjectToCharSequence("Incidencia"));
 //BA.debugLineNum = 342;BA.debugLine="lblInc.TextSize = 12";
_lblinc.setTextSize((float) (12));
 //BA.debugLineNum = 343;BA.debugLine="pnlContent.AddView(lblInc, 8dip, top, 84%x, 20dip";
mostCurrent._pnlcontent.AddView((android.view.View)(_lblinc.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),_top,anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (84),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)));
 //BA.debugLineNum = 345;BA.debugLine="curInc.Initialize(\"\")";
mostCurrent._curinc.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 346;BA.debugLine="curInc.SingleLine = False";
mostCurrent._curinc.setSingleLine(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 347;BA.debugLine="curInc.Text = savedInc";
mostCurrent._curinc.setText(BA.ObjectToCharSequence(_savedinc));
 //BA.debugLineNum = 348;BA.debugLine="pnlContent.AddView(curInc, 8dip, top + 20dip, 84%";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._curinc.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),(int) (_top+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (84),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (70)));
 //BA.debugLineNum = 351;BA.debugLine="btnPrev.Enabled = (index > 0)";
mostCurrent._btnprev.setEnabled((_index>0));
 //BA.debugLineNum = 352;BA.debugLine="btnNext.Enabled = (index < carsList.Size - 1)";
mostCurrent._btnnext.setEnabled((_index<mostCurrent._carslist.getSize()-1));
 //BA.debugLineNum = 353;BA.debugLine="currentIndex = index";
_currentindex = _index;
 //BA.debugLineNum = 354;BA.debugLine="End Sub";
return "";
}
}
