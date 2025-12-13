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
public static boolean _allowedit = false;
public anywheresoftware.b4a.objects.ScrollViewWrapper _sv = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.menuprincipal _menuprincipal = null;
public b4a.example.reportsbyarea _reportsbyarea = null;
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
 //BA.debugLineNum = 21;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 22;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 23;BA.debugLine="Activity.Title = \"Detalle reporte\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Detalle reporte"));
 //BA.debugLineNum = 25;BA.debugLine="sv.Initialize(100%y)";
mostCurrent._sv.Initialize(mostCurrent.activityBA,anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 26;BA.debugLine="Activity.AddView(sv, 0, 0, 100%x, 100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._sv.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 27;BA.debugLine="pnl = sv.Panel";
mostCurrent._pnl = mostCurrent._sv.getPanel();
 //BA.debugLineNum = 28;BA.debugLine="pnl.RemoveAllViews";
mostCurrent._pnl.RemoveAllViews();
 //BA.debugLineNum = 30;BA.debugLine="LoadAndRenderCurrentReport";
_loadandrendercurrentreport();
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 33;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _addactionbuttons(int _top) throws Exception{
anywheresoftware.b4a.objects.ButtonWrapper _btnedit = null;
anywheresoftware.b4a.objects.ButtonWrapper _btndelete = null;
 //BA.debugLineNum = 485;BA.debugLine="Sub AddActionButtons(top As Int)";
 //BA.debugLineNum = 486;BA.debugLine="Dim btnEdit As Button";
_btnedit = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 487;BA.debugLine="btnEdit.Initialize(\"btnEdit\")";
_btnedit.Initialize(mostCurrent.activityBA,"btnEdit");
 //BA.debugLineNum = 488;BA.debugLine="btnEdit.Text = \"Editar Reporte\"";
_btnedit.setText(BA.ObjectToCharSequence("Editar Reporte"));
 //BA.debugLineNum = 489;BA.debugLine="btnEdit.Color = Colors.RGB(255, 193, 7) ' Amber/O";
_btnedit.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (255),(int) (193),(int) (7)));
 //BA.debugLineNum = 490;BA.debugLine="btnEdit.TextColor = Colors.Black";
_btnedit.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 491;BA.debugLine="pnl.AddView(btnEdit, 10dip, top, 45%x, 50dip)";
mostCurrent._pnl.AddView((android.view.View)(_btnedit.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),_top,anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (45),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 493;BA.debugLine="Dim btnDelete As Button";
_btndelete = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 494;BA.debugLine="btnDelete.Initialize(\"btnDelete\")";
_btndelete.Initialize(mostCurrent.activityBA,"btnDelete");
 //BA.debugLineNum = 495;BA.debugLine="btnDelete.Text = \"Eliminar\"";
_btndelete.setText(BA.ObjectToCharSequence("Eliminar"));
 //BA.debugLineNum = 496;BA.debugLine="btnDelete.Color = Colors.RGB(220, 53, 69) ' Red";
_btndelete.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (220),(int) (53),(int) (69)));
 //BA.debugLineNum = 497;BA.debugLine="btnDelete.TextColor = Colors.White";
_btndelete.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 498;BA.debugLine="pnl.AddView(btnDelete, 55%x, top, 40%x, 50dip)";
mostCurrent._pnl.AddView((android.view.View)(_btndelete.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (55),mostCurrent.activityBA),_top,anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (40),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 500;BA.debugLine="pnl.Height = top + 70dip";
mostCurrent._pnl.setHeight((int) (_top+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (70))));
 //BA.debugLineNum = 501;BA.debugLine="End Sub";
return "";
}
public static void  _btndelete_click() throws Exception{
ResumableSub_btnDelete_Click rsub = new ResumableSub_btnDelete_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnDelete_Click extends BA.ResumableSub {
public ResumableSub_btnDelete_Click(b4a.example.reportdetail parent) {
this.parent = parent;
}
b4a.example.reportdetail parent;
int _result = 0;
String _jsonraw = "";
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _report = null;
int _id = 0;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 572;BA.debugLine="Msgbox2Async(\"¿Seguro que deseas eliminar este re";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("¿Seguro que deseas eliminar este reporte?"),BA.ObjectToCharSequence("Eliminar"),"Sí","Cancelar","",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),processBA,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 573;BA.debugLine="Wait For Msgbox_Result (Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, null);
this.state = 11;
return;
case 11:
//C
this.state = 1;
_result = (Integer) result[0];
;
 //BA.debugLineNum = 574;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
if (true) break;

case 1:
//if
this.state = 10;
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 575;BA.debugLine="Dim jsonRaw As String = File.ReadString(File.Dir";
_jsonraw = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json");
 //BA.debugLineNum = 576;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 577;BA.debugLine="parser.Initialize(jsonRaw)";
_parser.Initialize(_jsonraw);
 //BA.debugLineNum = 578;BA.debugLine="Dim report As Map = parser.NextObject";
_report = new anywheresoftware.b4a.objects.collections.Map();
_report = _parser.NextObject();
 //BA.debugLineNum = 580;BA.debugLine="If report.ContainsKey(\"Id_Reporte\") Then";
if (true) break;

case 4:
//if
this.state = 9;
if (_report.ContainsKey((Object)("Id_Reporte"))) { 
this.state = 6;
}else {
this.state = 8;
}if (true) break;

case 6:
//C
this.state = 9;
 //BA.debugLineNum = 581;BA.debugLine="Dim id As Int = report.Get(\"Id_Reporte\")";
_id = (int)(BA.ObjectToNumber(_report.Get((Object)("Id_Reporte"))));
 //BA.debugLineNum = 582;BA.debugLine="DeleteReport(id)";
_deletereport(_id);
 if (true) break;

case 8:
//C
this.state = 9;
 //BA.debugLineNum = 584;BA.debugLine="ToastMessageShow(\"No se encontró Id_Reporte.\",";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se encontró Id_Reporte."),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 9:
//C
this.state = 10;
;
 if (true) break;

case 10:
//C
this.state = -1;
;
 //BA.debugLineNum = 587;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _msgbox_result(int _result) throws Exception{
}
public static String  _btnedit_click() throws Exception{
String _jsonraw = "";
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _report = null;
anywheresoftware.b4a.objects.collections.Map _areamap = null;
int _idarea = 0;
 //BA.debugLineNum = 503;BA.debugLine="Sub btnEdit_Click";
 //BA.debugLineNum = 504;BA.debugLine="Dim jsonRaw As String = File.ReadString(File.DirI";
_jsonraw = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json");
 //BA.debugLineNum = 505;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 506;BA.debugLine="parser.Initialize(jsonRaw)";
_parser.Initialize(_jsonraw);
 //BA.debugLineNum = 507;BA.debugLine="Dim report As Map = parser.NextObject";
_report = new anywheresoftware.b4a.objects.collections.Map();
_report = _parser.NextObject();
 //BA.debugLineNum = 509;BA.debugLine="If report.ContainsKey(\"Id_Reporte\") = False Or re";
if (_report.ContainsKey((Object)("Id_Reporte"))==anywheresoftware.b4a.keywords.Common.False || _report.ContainsKey((Object)("area"))==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 510;BA.debugLine="ToastMessageShow(\"Datos incompletos para editar.";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Datos incompletos para editar."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 511;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 515;BA.debugLine="Dim areaMap As Map = report.Get(\"area\")";
_areamap = new anywheresoftware.b4a.objects.collections.Map();
_areamap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_report.Get((Object)("area"))));
 //BA.debugLineNum = 517;BA.debugLine="If areaMap.ContainsKey(\"cars\") And IsListValid(ar";
if (_areamap.ContainsKey((Object)("cars")) && _islistvalid(_areamap.Get((Object)("cars")))) { 
 //BA.debugLineNum = 519;BA.debugLine="OpenEditDialog(report)";
_openeditdialog(_report);
 }else {
 //BA.debugLineNum = 522;BA.debugLine="If report.ContainsKey(\"Id_Area\") Then";
if (_report.ContainsKey((Object)("Id_Area"))) { 
 //BA.debugLineNum = 523;BA.debugLine="Dim idArea As Int = report.Get(\"Id_Reporte\") '";
_idarea = (int)(BA.ObjectToNumber(_report.Get((Object)("Id_Reporte"))));
 //BA.debugLineNum = 524;BA.debugLine="If report.ContainsKey(\"Id_Area\") Then idArea =";
if (_report.ContainsKey((Object)("Id_Area"))) { 
_idarea = (int)(BA.ObjectToNumber(_report.Get((Object)("Id_Area"))));};
 //BA.debugLineNum = 526;BA.debugLine="ProgressDialogShow(\"Obteniendo datos del área p";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Obteniendo datos del área para editar..."));
 //BA.debugLineNum = 527;BA.debugLine="FetchAreaForEdit(idArea, report)";
_fetchareaforedit(_idarea,_report);
 }else {
 //BA.debugLineNum = 529;BA.debugLine="ToastMessageShow(\"No se puede editar: falta inf";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se puede editar: falta información de área y CARs."),anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 532;BA.debugLine="End Sub";
return "";
}
public static String  _deletereport(int _id) throws Exception{
b4a.example.httpjob _job = null;
String _url = "";
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _json = null;
anywheresoftware.b4a.objects.collections.Map _m = null;
 //BA.debugLineNum = 589;BA.debugLine="Sub DeleteReport(id As Int)";
 //BA.debugLineNum = 590;BA.debugLine="Dim job As HttpJob";
_job = new b4a.example.httpjob();
 //BA.debugLineNum = 591;BA.debugLine="job.Initialize(\"DeleteJob\", Me)";
_job._initialize /*String*/ (processBA,"DeleteJob",reportdetail.getObject());
 //BA.debugLineNum = 592;BA.debugLine="Dim url As String = \"https://humane-pelican-brief";
_url = "https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api/delete_report.php";
 //BA.debugLineNum = 593;BA.debugLine="Dim json As JSONGenerator";
_json = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 594;BA.debugLine="Dim m As Map";
_m = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 595;BA.debugLine="m.Initialize";
_m.Initialize();
 //BA.debugLineNum = 596;BA.debugLine="m.Put(\"Id_Reporte\", id)";
_m.Put((Object)("Id_Reporte"),(Object)(_id));
 //BA.debugLineNum = 597;BA.debugLine="json.Initialize(m)";
_json.Initialize(_m);
 //BA.debugLineNum = 598;BA.debugLine="job.PostString(url, json.ToString)";
_job._poststring /*String*/ (_url,_json.ToString());
 //BA.debugLineNum = 599;BA.debugLine="End Sub";
return "";
}
public static int  _estimatelabelheight(String _text,int _textsize,int _width) throws Exception{
int _charwidth = 0;
int _charsperline = 0;
int _textlen = 0;
int _lines = 0;
int _lineheight = 0;
int _result = 0;
 //BA.debugLineNum = 694;BA.debugLine="Sub EstimateLabelHeight(text As String, textSize A";
 //BA.debugLineNum = 695;BA.debugLine="If text = \"\" Then Return 30dip";
if ((_text).equals("")) { 
if (true) return anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30));};
 //BA.debugLineNum = 696;BA.debugLine="If width <= 0 Then width = 100dip";
if (_width<=0) { 
_width = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100));};
 //BA.debugLineNum = 697;BA.debugLine="Dim charWidth As Int = (textSize * 6) / 10";
_charwidth = (int) ((_textsize*6)/(double)10);
 //BA.debugLineNum = 698;BA.debugLine="If charWidth < 4 Then charWidth = 4";
if (_charwidth<4) { 
_charwidth = (int) (4);};
 //BA.debugLineNum = 699;BA.debugLine="Dim charsPerLine As Int = width / charWidth";
_charsperline = (int) (_width/(double)_charwidth);
 //BA.debugLineNum = 700;BA.debugLine="If charsPerLine < 1 Then charsPerLine = 1";
if (_charsperline<1) { 
_charsperline = (int) (1);};
 //BA.debugLineNum = 701;BA.debugLine="Dim textLen As Int = text.Length";
_textlen = _text.length();
 //BA.debugLineNum = 702;BA.debugLine="Dim lines As Int = textLen / charsPerLine";
_lines = (int) (_textlen/(double)_charsperline);
 //BA.debugLineNum = 703;BA.debugLine="If (textLen Mod charsPerLine) <> 0 Then lines = l";
if ((_textlen%_charsperline)!=0) { 
_lines = (int) (_lines+1);};
 //BA.debugLineNum = 704;BA.debugLine="Dim lineHeight As Int = (textSize * 16) / 10";
_lineheight = (int) ((_textsize*16)/(double)10);
 //BA.debugLineNum = 705;BA.debugLine="If lineHeight < 14 Then lineHeight = 14";
if (_lineheight<14) { 
_lineheight = (int) (14);};
 //BA.debugLineNum = 706;BA.debugLine="Dim result As Int = lines * lineHeight + 6dip";
_result = (int) (_lines*_lineheight+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 //BA.debugLineNum = 707;BA.debugLine="Return result";
if (true) return _result;
 //BA.debugLineNum = 708;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 190;BA.debugLine="Sub ExtractFirstJson(s As String) As String";
 //BA.debugLineNum = 191;BA.debugLine="If s = Null Then Return \"\"";
if (_s== null) { 
if (true) return "";};
 //BA.debugLineNum = 192;BA.debugLine="s = s.Trim";
_s = _s.trim();
 //BA.debugLineNum = 193;BA.debugLine="Dim len As Int = s.Length";
_len = _s.length();
 //BA.debugLineNum = 194;BA.debugLine="If len = 0 Then Return \"\"";
if (_len==0) { 
if (true) return "";};
 //BA.debugLineNum = 196;BA.debugLine="Dim firstObj As Int = s.IndexOf(\"{\")";
_firstobj = _s.indexOf("{");
 //BA.debugLineNum = 197;BA.debugLine="Dim firstArr As Int = s.IndexOf(\"[\")";
_firstarr = _s.indexOf("[");
 //BA.debugLineNum = 198;BA.debugLine="Dim start As Int";
_start = 0;
 //BA.debugLineNum = 199;BA.debugLine="If firstObj = -1 And firstArr = -1 Then Return \"\"";
if (_firstobj==-1 && _firstarr==-1) { 
if (true) return "";};
 //BA.debugLineNum = 200;BA.debugLine="If firstObj = -1 Then";
if (_firstobj==-1) { 
 //BA.debugLineNum = 201;BA.debugLine="start = firstArr";
_start = _firstarr;
 }else if(_firstarr==-1) { 
 //BA.debugLineNum = 203;BA.debugLine="start = firstObj";
_start = _firstobj;
 }else {
 //BA.debugLineNum = 205;BA.debugLine="If firstObj < firstArr Then";
if (_firstobj<_firstarr) { 
 //BA.debugLineNum = 206;BA.debugLine="start = firstObj";
_start = _firstobj;
 }else {
 //BA.debugLineNum = 208;BA.debugLine="start = firstArr";
_start = _firstarr;
 };
 };
 //BA.debugLineNum = 212;BA.debugLine="Dim depth As Int = 0";
_depth = (int) (0);
 //BA.debugLineNum = 213;BA.debugLine="Dim inString As Boolean = False";
_instring = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 214;BA.debugLine="Dim escaped As Boolean = False";
_escaped = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 216;BA.debugLine="For i = start To len - 1";
{
final int step23 = 1;
final int limit23 = (int) (_len-1);
_i = _start ;
for (;_i <= limit23 ;_i = _i + step23 ) {
 //BA.debugLineNum = 217;BA.debugLine="Dim ch As String = s.SubString2(i, i + 1)";
_ch = _s.substring(_i,(int) (_i+1));
 //BA.debugLineNum = 218;BA.debugLine="If escaped Then";
if (_escaped) { 
 //BA.debugLineNum = 219;BA.debugLine="escaped = False";
_escaped = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 220;BA.debugLine="Continue";
if (true) continue;
 };
 //BA.debugLineNum = 222;BA.debugLine="If ch = \"\\\" Then";
if ((_ch).equals("\\")) { 
 //BA.debugLineNum = 223;BA.debugLine="escaped = True";
_escaped = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 224;BA.debugLine="Continue";
if (true) continue;
 };
 //BA.debugLineNum = 226;BA.debugLine="If ch = \"\"\"\" Then";
if ((_ch).equals("\"")) { 
 //BA.debugLineNum = 227;BA.debugLine="inString = Not(inString)";
_instring = anywheresoftware.b4a.keywords.Common.Not(_instring);
 //BA.debugLineNum = 228;BA.debugLine="Continue";
if (true) continue;
 };
 //BA.debugLineNum = 231;BA.debugLine="If Not(inString) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_instring)) { 
 //BA.debugLineNum = 232;BA.debugLine="If ch = \"{\" Or ch = \"[\" Then";
if ((_ch).equals("{") || (_ch).equals("[")) { 
 //BA.debugLineNum = 233;BA.debugLine="depth = depth + 1";
_depth = (int) (_depth+1);
 }else if((_ch).equals("}") || (_ch).equals("]")) { 
 //BA.debugLineNum = 235;BA.debugLine="depth = depth - 1";
_depth = (int) (_depth-1);
 //BA.debugLineNum = 236;BA.debugLine="If depth = 0 Then";
if (_depth==0) { 
 //BA.debugLineNum = 237;BA.debugLine="Return s.SubString2(start, i + 1)";
if (true) return _s.substring(_start,(int) (_i+1));
 };
 };
 };
 }
};
 //BA.debugLineNum = 243;BA.debugLine="Return \"\"";
if (true) return "";
 //BA.debugLineNum = 244;BA.debugLine="End Sub";
return "";
}
public static String  _fetchareaforedit(int _idarea,anywheresoftware.b4a.objects.collections.Map _reportmap) throws Exception{
b4a.example.httpjob _job = null;
String _body = "";
 //BA.debugLineNum = 542;BA.debugLine="Sub FetchAreaForEdit(idArea As Int, reportMap As M";
 //BA.debugLineNum = 543;BA.debugLine="Dim job As HttpJob";
_job = new b4a.example.httpjob();
 //BA.debugLineNum = 544;BA.debugLine="job.Initialize(\"GetAreaForEdit\", Me)";
_job._initialize /*String*/ (processBA,"GetAreaForEdit",reportdetail.getObject());
 //BA.debugLineNum = 546;BA.debugLine="job.Tag = reportMap";
_job._tag /*Object*/  = (Object)(_reportmap.getObject());
 //BA.debugLineNum = 547;BA.debugLine="Dim body As String = \"codigo=\" & idArea ' getArea";
_body = "codigo="+BA.NumberToString(_idarea);
 //BA.debugLineNum = 548;BA.debugLine="job.PostString(\"https://humane-pelican-briefly.ng";
_job._poststring /*String*/ ("https://humane-pelican-briefly.ngrok-free.app/Proyecto_QR/api/getArea.php",_body);
 //BA.debugLineNum = 549;BA.debugLine="End Sub";
return "";
}
public static String  _formatvalue(Object _v) throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _jg = null;
 //BA.debugLineNum = 669;BA.debugLine="Sub FormatValue(v As Object) As String";
 //BA.debugLineNum = 670;BA.debugLine="If v = Null Then Return \"\"";
if (_v== null) { 
if (true) return "";};
 //BA.debugLineNum = 671;BA.debugLine="Try";
try { //BA.debugLineNum = 673;BA.debugLine="If v = True Then Return \"Correcto\"";
if ((_v).equals((Object)(anywheresoftware.b4a.keywords.Common.True))) { 
if (true) return "Correcto";};
 //BA.debugLineNum = 674;BA.debugLine="If v = False Then Return \"No correcto\"";
if ((_v).equals((Object)(anywheresoftware.b4a.keywords.Common.False))) { 
if (true) return "No correcto";};
 //BA.debugLineNum = 677;BA.debugLine="If v Is Map Or v Is List Then";
if (_v instanceof java.util.Map || _v instanceof java.util.List) { 
 //BA.debugLineNum = 678;BA.debugLine="Dim jg As JSONGenerator";
_jg = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 679;BA.debugLine="jg.Initialize(v)";
_jg.Initialize((anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_v)));
 //BA.debugLineNum = 680;BA.debugLine="Return jg.ToString";
if (true) return _jg.ToString();
 };
 //BA.debugLineNum = 684;BA.debugLine="Return v";
if (true) return BA.ObjectToString(_v);
 } 
       catch (Exception e12) {
			processBA.setLastException(e12); //BA.debugLineNum = 686;BA.debugLine="Log(\"FormatValue error: \" & LastException.Messag";
anywheresoftware.b4a.keywords.Common.LogImpl("33145745","FormatValue error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 687;BA.debugLine="Return \"\"";
if (true) return "";
 };
 //BA.debugLineNum = 689;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 17;BA.debugLine="Private sv As ScrollView";
mostCurrent._sv = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private pnl As Panel";
mostCurrent._pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static boolean  _islistvalid(Object _l) throws Exception{
anywheresoftware.b4a.objects.collections.List _lst = null;
 //BA.debugLineNum = 534;BA.debugLine="Sub IsListValid(l As Object) As Boolean";
 //BA.debugLineNum = 535;BA.debugLine="If l Is List Then";
if (_l instanceof java.util.List) { 
 //BA.debugLineNum = 536;BA.debugLine="Dim lst As List = l";
_lst = new anywheresoftware.b4a.objects.collections.List();
_lst = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_l));
 //BA.debugLineNum = 537;BA.debugLine="Return lst.Size > 0";
if (true) return _lst.getSize()>0;
 };
 //BA.debugLineNum = 539;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 540;BA.debugLine="End Sub";
return false;
}
public static String  _jobdone(b4a.example.httpjob _job) throws Exception{
String _res = "";
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _areadata = null;
String _jas = "";
anywheresoftware.b4a.objects.collections.JSONParser _jp = null;
anywheresoftware.b4a.objects.collections.Map _fullarea = null;
anywheresoftware.b4a.objects.collections.Map _originalreport = null;
 //BA.debugLineNum = 601;BA.debugLine="Sub JobDone(Job As HttpJob)";
 //BA.debugLineNum = 602;BA.debugLine="If Job.JobName = \"DeleteJob\" Then";
if ((_job._jobname /*String*/ ).equals("DeleteJob")) { 
 //BA.debugLineNum = 603;BA.debugLine="If Job.Success Then";
if (_job._success /*boolean*/ ) { 
 //BA.debugLineNum = 604;BA.debugLine="ToastMessageShow(\"Reporte eliminado.\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Reporte eliminado."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 605;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 }else {
 //BA.debugLineNum = 607;BA.debugLine="ToastMessageShow(\"Error eliminando: \" & Job.Err";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error eliminando: "+_job._errormessage /*String*/ ),anywheresoftware.b4a.keywords.Common.True);
 };
 }else if((_job._jobname /*String*/ ).equals("GetAreaForEdit")) { 
 //BA.debugLineNum = 610;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 611;BA.debugLine="If Job.Success Then";
if (_job._success /*boolean*/ ) { 
 //BA.debugLineNum = 612;BA.debugLine="Dim res As String = Job.GetString";
_res = _job._getstring /*String*/ ();
 //BA.debugLineNum = 614;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 615;BA.debugLine="parser.Initialize(res)";
_parser.Initialize(_res);
 //BA.debugLineNum = 616;BA.debugLine="Try";
try { //BA.debugLineNum = 617;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = _parser.NextObject();
 //BA.debugLineNum = 619;BA.debugLine="Dim areaData As Map";
_areadata = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 620;BA.debugLine="If root.ContainsKey(\"success\") And root.Get(\"s";
if (_root.ContainsKey((Object)("success")) && (_root.Get((Object)("success"))).equals((Object)(anywheresoftware.b4a.keywords.Common.True))) { 
 //BA.debugLineNum = 621;BA.debugLine="areaData = root.Get(\"data\")";
_areadata = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_root.Get((Object)("data"))));
 }else {
 //BA.debugLineNum = 623;BA.debugLine="areaData = root";
_areadata = _root;
 };
 //BA.debugLineNum = 627;BA.debugLine="If areaData.IsInitialized = False Or (areaData";
if (_areadata.IsInitialized()==anywheresoftware.b4a.keywords.Common.False || (_areadata.ContainsKey((Object)("cars"))==anywheresoftware.b4a.keywords.Common.False && _areadata.ContainsKey((Object)("json_area"))==anywheresoftware.b4a.keywords.Common.False)) { 
 //BA.debugLineNum = 628;BA.debugLine="ToastMessageShow(\"El área no tiene definición";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("El área no tiene definición de CARs válida."),anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 631;BA.debugLine="If areaData.ContainsKey(\"JSON_Area\") Then";
if (_areadata.ContainsKey((Object)("JSON_Area"))) { 
 //BA.debugLineNum = 632;BA.debugLine="Try";
try { //BA.debugLineNum = 633;BA.debugLine="Dim jas As String = areaData.Get(\"JSON_Area";
_jas = BA.ObjectToString(_areadata.Get((Object)("JSON_Area")));
 //BA.debugLineNum = 634;BA.debugLine="Dim jp As JSONParser";
_jp = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 635;BA.debugLine="jp.Initialize(jas)";
_jp.Initialize(_jas);
 //BA.debugLineNum = 636;BA.debugLine="Dim fullArea As Map = jp.NextObject";
_fullarea = new anywheresoftware.b4a.objects.collections.Map();
_fullarea = _jp.NextObject();
 //BA.debugLineNum = 638;BA.debugLine="If fullArea.ContainsKey(\"cars\") Then areaDa";
if (_fullarea.ContainsKey((Object)("cars"))) { 
_areadata.Put((Object)("cars"),_fullarea.Get((Object)("cars")));};
 } 
       catch (Exception e33) {
			processBA.setLastException(e33); //BA.debugLineNum = 640;BA.debugLine="Log(\"Error parsing JSON_Area for edit: \" &";
anywheresoftware.b4a.keywords.Common.LogImpl("33080231","Error parsing JSON_Area for edit: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 645;BA.debugLine="Dim originalReport As Map = Job.Tag";
_originalreport = new anywheresoftware.b4a.objects.collections.Map();
_originalreport = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_job._tag /*Object*/ ));
 //BA.debugLineNum = 646;BA.debugLine="originalReport.Put(\"area\", areaData)";
_originalreport.Put((Object)("area"),(Object)(_areadata.getObject()));
 //BA.debugLineNum = 649;BA.debugLine="OpenEditDialog(originalReport)";
_openeditdialog(_originalreport);
 };
 } 
       catch (Exception e41) {
			processBA.setLastException(e41); //BA.debugLineNum = 652;BA.debugLine="Log(\"Error parsing GetAreaForEdit response: \"";
anywheresoftware.b4a.keywords.Common.LogImpl("33080243","Error parsing GetAreaForEdit response: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 653;BA.debugLine="ToastMessageShow(\"Error procesando datos del á";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error procesando datos del área."),anywheresoftware.b4a.keywords.Common.True);
 };
 }else {
 //BA.debugLineNum = 656;BA.debugLine="ToastMessageShow(\"No se pudo obtener datos del";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se pudo obtener datos del área: "+_job._errormessage /*String*/ ),anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 659;BA.debugLine="Job.Release";
_job._release /*String*/ ();
 //BA.debugLineNum = 660;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 45;BA.debugLine="Sub LoadAndRenderCurrentReport";
 //BA.debugLineNum = 46;BA.debugLine="Dim jsonToUse As String = ReportJson";
_jsontouse = _reportjson;
 //BA.debugLineNum = 47;BA.debugLine="If jsonToUse = \"\" Then";
if ((_jsontouse).equals("")) { 
 //BA.debugLineNum = 48;BA.debugLine="Try";
try { //BA.debugLineNum = 49;BA.debugLine="If File.Exists(File.DirInternal, \"current_repor";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json")) { 
 //BA.debugLineNum = 50;BA.debugLine="jsonToUse = File.ReadString(File.DirInternal,";
_jsontouse = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"current_report.json");
 };
 } 
       catch (Exception e8) {
			processBA.setLastException(e8); //BA.debugLineNum = 53;BA.debugLine="Log(\"ReportDetail: Error reading current_report";
anywheresoftware.b4a.keywords.Common.LogImpl("32424840","ReportDetail: Error reading current_report.json: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 57;BA.debugLine="If jsonToUse = Null Then jsonToUse = \"\"";
if (_jsontouse== null) { 
_jsontouse = "";};
 //BA.debugLineNum = 58;BA.debugLine="jsonToUse = jsonToUse.Trim";
_jsontouse = _jsontouse.trim();
 //BA.debugLineNum = 59;BA.debugLine="If jsonToUse = \"\" Then";
if ((_jsontouse).equals("")) { 
 //BA.debugLineNum = 60;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 61;BA.debugLine="lbl.Initialize(\"lbl_empty\")";
_lbl.Initialize(mostCurrent.activityBA,"lbl_empty");
 //BA.debugLineNum = 62;BA.debugLine="lbl.Text = \"No hay datos para mostrar.\"";
_lbl.setText(BA.ObjectToCharSequence("No hay datos para mostrar."));
 //BA.debugLineNum = 63;BA.debugLine="lbl.TextSize = 16";
_lbl.setTextSize((float) (16));
 //BA.debugLineNum = 64;BA.debugLine="pnl.AddView(lbl, 10dip, 10dip, 100%x - 20dip, Es";
mostCurrent._pnl.AddView((android.view.View)(_lbl.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),_estimatelabelheight(_lbl.getText(),(int) (_lbl.getTextSize()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)))));
 //BA.debugLineNum = 65;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 69;BA.debugLine="Try";
try { //BA.debugLineNum = 70;BA.debugLine="jsonToUse = jsonToUse.Replace(Chr(65279), \"\")";
_jsontouse = _jsontouse.replace(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (65279))),"");
 } 
       catch (Exception e24) {
			processBA.setLastException(e24); //BA.debugLineNum = 72;BA.debugLine="Log(\"ReportDetail: BOM replace error: \" & LastEx";
anywheresoftware.b4a.keywords.Common.LogImpl("32424859","ReportDetail: BOM replace error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 76;BA.debugLine="Dim extracted As String = ExtractFirstJson(jsonTo";
_extracted = _extractfirstjson(_jsontouse);
 //BA.debugLineNum = 77;BA.debugLine="If extracted = \"\" Then";
if ((_extracted).equals("")) { 
 //BA.debugLineNum = 78;BA.debugLine="Log(\"ReportDetail: no se encontró bloque JSON co";
anywheresoftware.b4a.keywords.Common.LogImpl("32424865","ReportDetail: no se encontró bloque JSON completo. Snippet: "+_jsontouse.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(_jsontouse.length(),800))),0);
 //BA.debugLineNum = 79;BA.debugLine="ToastMessageShow(\"Formato de reporte inválido (v";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Formato de reporte inválido (ver logs)."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 80;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 84;BA.debugLine="Dim sb As StringBuilder";
_sb = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 85;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 86;BA.debugLine="For i = 0 To extracted.Length - 1";
{
final int step34 = 1;
final int limit34 = (int) (_extracted.length()-1);
_i = (int) (0) ;
for (;_i <= limit34 ;_i = _i + step34 ) {
 //BA.debugLineNum = 87;BA.debugLine="Dim ch As String = extracted.SubString2(i, i + 1";
_ch = _extracted.substring(_i,(int) (_i+1));
 //BA.debugLineNum = 88;BA.debugLine="Dim b() As Byte = ch.GetBytes(\"UTF8\")";
_b = _ch.getBytes("UTF8");
 //BA.debugLineNum = 89;BA.debugLine="Dim code As Int = 32";
_code = (int) (32);
 //BA.debugLineNum = 90;BA.debugLine="If b.Length > 0 Then";
if (_b.length>0) { 
 //BA.debugLineNum = 91;BA.debugLine="code = b(0)";
_code = (int) (_b[(int) (0)]);
 //BA.debugLineNum = 92;BA.debugLine="If code < 0 Then code = code + 256";
if (_code<0) { 
_code = (int) (_code+256);};
 };
 //BA.debugLineNum = 94;BA.debugLine="If code >= 32 Or code = 9 Or code = 10 Or code =";
if (_code>=32 || _code==9 || _code==10 || _code==13) { 
 //BA.debugLineNum = 95;BA.debugLine="sb.Append(ch)";
_sb.Append(_ch);
 };
 }
};
 //BA.debugLineNum = 98;BA.debugLine="Dim cleaned As String = sb.ToString";
_cleaned = _sb.ToString();
 //BA.debugLineNum = 101;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 102;BA.debugLine="Dim root As Object";
_root = new Object();
 //BA.debugLineNum = 103;BA.debugLine="Try";
try { //BA.debugLineNum = 104;BA.debugLine="parser.Initialize(cleaned)";
_parser.Initialize(_cleaned);
 //BA.debugLineNum = 105;BA.debugLine="If cleaned.StartsWith(\"{\") Then";
if (_cleaned.startsWith("{")) { 
 //BA.debugLineNum = 106;BA.debugLine="root = parser.NextObject";
_root = (Object)(_parser.NextObject().getObject());
 //BA.debugLineNum = 107;BA.debugLine="Log(\"ReportDetail: Parsed cleaned as JSON Objec";
anywheresoftware.b4a.keywords.Common.LogImpl("32424894","ReportDetail: Parsed cleaned as JSON Object",0);
 }else if(_cleaned.startsWith("[")) { 
 //BA.debugLineNum = 109;BA.debugLine="root = parser.NextArray";
_root = (Object)(_parser.NextArray().getObject());
 //BA.debugLineNum = 110;BA.debugLine="Log(\"ReportDetail: Parsed cleaned as JSON Array";
anywheresoftware.b4a.keywords.Common.LogImpl("32424897","ReportDetail: Parsed cleaned as JSON Array",0);
 }else {
 //BA.debugLineNum = 112;BA.debugLine="Log(\"ReportDetail: cleaned no empieza con { ni";
anywheresoftware.b4a.keywords.Common.LogImpl("32424899","ReportDetail: cleaned no empieza con { ni [",0);
 //BA.debugLineNum = 113;BA.debugLine="root = Null";
_root = anywheresoftware.b4a.keywords.Common.Null;
 };
 } 
       catch (Exception e62) {
			processBA.setLastException(e62); //BA.debugLineNum = 116;BA.debugLine="Log(\"ReportDetail: parse error: \" & LastExceptio";
anywheresoftware.b4a.keywords.Common.LogImpl("32424903","ReportDetail: parse error: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 117;BA.debugLine="root = Null";
_root = anywheresoftware.b4a.keywords.Common.Null;
 };
 //BA.debugLineNum = 120;BA.debugLine="If root = Null Then";
if (_root== null) { 
 //BA.debugLineNum = 121;BA.debugLine="Try";
try { //BA.debugLineNum = 122;BA.debugLine="Dim snippet As String = cleaned";
_snippet = _cleaned;
 //BA.debugLineNum = 123;BA.debugLine="If snippet.Length > 2000 Then snippet = snippet";
if (_snippet.length()>2000) { 
_snippet = _snippet.substring((int) (0),(int) (2000));};
 //BA.debugLineNum = 124;BA.debugLine="Log(\"ReportDetail: FINAL_PARSE_FAILED_SNIPPET:";
anywheresoftware.b4a.keywords.Common.LogImpl("32424911","ReportDetail: FINAL_PARSE_FAILED_SNIPPET: "+_snippet,0);
 } 
       catch (Exception e71) {
			processBA.setLastException(e71); //BA.debugLineNum = 126;BA.debugLine="Log(LastException.Message)";
anywheresoftware.b4a.keywords.Common.LogImpl("32424913",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 128;BA.debugLine="ToastMessageShow(\"No se pudo parsear detalle (ve";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se pudo parsear detalle (ver Logcat)."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 129;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 133;BA.debugLine="Dim rp As Map";
_rp = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 134;BA.debugLine="rp = Null";
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 135;BA.debugLine="If root Is Map Then";
if (_root instanceof java.util.Map) { 
 //BA.debugLineNum = 136;BA.debugLine="rp = root";
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_root));
 }else if(_root instanceof java.util.List) { 
 //BA.debugLineNum = 138;BA.debugLine="Dim lst As List = root";
_lst = new anywheresoftware.b4a.objects.collections.List();
_lst = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_root));
 //BA.debugLineNum = 139;BA.debugLine="If lst.Size > 0 And lst.Get(0) Is Map Then";
if (_lst.getSize()>0 && _lst.Get((int) (0)) instanceof java.util.Map) { 
 //BA.debugLineNum = 140;BA.debugLine="rp = lst.Get(0)";
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_lst.Get((int) (0))));
 }else {
 //BA.debugLineNum = 142;BA.debugLine="Log(\"ReportDetail: JSON lista sin objetos Map.\"";
anywheresoftware.b4a.keywords.Common.LogImpl("32424929","ReportDetail: JSON lista sin objetos Map.",0);
 //BA.debugLineNum = 143;BA.debugLine="ToastMessageShow(\"Formato de reporte inesperado";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Formato de reporte inesperado."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 144;BA.debugLine="Return";
if (true) return "";
 };
 }else {
 //BA.debugLineNum = 147;BA.debugLine="Log(\"ReportDetail: root no es Map ni List.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("32424934","ReportDetail: root no es Map ni List.",0);
 //BA.debugLineNum = 148;BA.debugLine="ToastMessageShow(\"Formato de reporte inesperado.";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Formato de reporte inesperado."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 149;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 153;BA.debugLine="Try";
try { //BA.debugLineNum = 154;BA.debugLine="If rp.ContainsKey(\"parsed\") Then";
if (_rp.ContainsKey((Object)("parsed"))) { 
 //BA.debugLineNum = 155;BA.debugLine="Dim maybeParsed As Object = rp.Get(\"parsed\")";
_maybeparsed = _rp.Get((Object)("parsed"));
 //BA.debugLineNum = 156;BA.debugLine="If maybeParsed Is Map Then rp = maybeParsed";
if (_maybeparsed instanceof java.util.Map) { 
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_maybeparsed));};
 };
 } 
       catch (Exception e100) {
			processBA.setLastException(e100); //BA.debugLineNum = 159;BA.debugLine="Log(LastException.Message)";
anywheresoftware.b4a.keywords.Common.LogImpl("32424946",anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 163;BA.debugLine="Try";
try { //BA.debugLineNum = 164;BA.debugLine="If rp.ContainsKey(\"area\") = False And rp.Contain";
if (_rp.ContainsKey((Object)("area"))==anywheresoftware.b4a.keywords.Common.False && _rp.ContainsKey((Object)("JSON_Reporte"))) { 
 //BA.debugLineNum = 165;BA.debugLine="Dim sj As String = rp.Get(\"JSON_Reporte\")";
_sj = BA.ObjectToString(_rp.Get((Object)("JSON_Reporte")));
 //BA.debugLineNum = 166;BA.debugLine="If sj <> \"\" Then";
if ((_sj).equals("") == false) { 
 //BA.debugLineNum = 167;BA.debugLine="Dim p2 As JSONParser";
_p2 = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 168;BA.debugLine="p2.Initialize(sj)";
_p2.Initialize(_sj);
 //BA.debugLineNum = 169;BA.debugLine="Dim inner As Object = p2.NextValue";
_inner = _p2.NextValue();
 //BA.debugLineNum = 170;BA.debugLine="If inner Is Map Then rp = inner";
if (_inner instanceof java.util.Map) { 
_rp = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_inner));};
 };
 };
 } 
       catch (Exception e113) {
			processBA.setLastException(e113); //BA.debugLineNum = 174;BA.debugLine="Log(\"ReportDetail: no se pudo parsear rp.JSON_Re";
anywheresoftware.b4a.keywords.Common.LogImpl("32424961","ReportDetail: no se pudo parsear rp.JSON_Reporte interno: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 //BA.debugLineNum = 178;BA.debugLine="Try";
try { //BA.debugLineNum = 179;BA.debugLine="RenderReport(rp)";
_renderreport(_rp);
 } 
       catch (Exception e118) {
			processBA.setLastException(e118); //BA.debugLineNum = 181;BA.debugLine="Log(\"ReportDetail: error en RenderReport: \" & La";
anywheresoftware.b4a.keywords.Common.LogImpl("32424968","ReportDetail: error en RenderReport: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 //BA.debugLineNum = 182;BA.debugLine="ToastMessageShow(\"No se pudo mostrar el reporte";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No se pudo mostrar el reporte (ver logs)."),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 184;BA.debugLine="End Sub";
return "";
}
public static String  _openeditdialog(anywheresoftware.b4a.objects.collections.Map _report) throws Exception{
anywheresoftware.b4a.objects.collections.Map _areamap = null;
anywheresoftware.b4a.objects.collections.Map _areawrapper = null;
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _jg = null;
 //BA.debugLineNum = 551;BA.debugLine="Sub OpenEditDialog(report As Map)";
 //BA.debugLineNum = 552;BA.debugLine="Dim areaMap As Map = report.Get(\"area\")";
_areamap = new anywheresoftware.b4a.objects.collections.Map();
_areamap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_report.Get((Object)("area"))));
 //BA.debugLineNum = 554;BA.debugLine="Dim areaWrapper As Map";
_areawrapper = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 555;BA.debugLine="areaWrapper.Initialize";
_areawrapper.Initialize();
 //BA.debugLineNum = 556;BA.debugLine="areaWrapper.Put(\"success\", True)";
_areawrapper.Put((Object)("success"),(Object)(anywheresoftware.b4a.keywords.Common.True));
 //BA.debugLineNum = 557;BA.debugLine="areaWrapper.Put(\"data\", areaMap)";
_areawrapper.Put((Object)("data"),(Object)(_areamap.getObject()));
 //BA.debugLineNum = 558;BA.debugLine="Dim jg As JSONGenerator";
_jg = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 559;BA.debugLine="jg.Initialize(areaWrapper)";
_jg.Initialize(_areawrapper);
 //BA.debugLineNum = 560;BA.debugLine="File.WriteString(File.DirInternal, \"last_area.jso";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"last_area.json",_jg.ToString());
 //BA.debugLineNum = 563;BA.debugLine="ReportDialog.IsEditing = True";
mostCurrent._reportdialog._isediting /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 564;BA.debugLine="ReportDialog.EditReportId = report.Get(\"Id_Report";
mostCurrent._reportdialog._editreportid /*int*/  = (int)(BA.ObjectToNumber(_report.Get((Object)("Id_Reporte"))));
 //BA.debugLineNum = 565;BA.debugLine="ReportDialog.PreloadedReport = report ' Pasamos t";
mostCurrent._reportdialog._preloadedreport /*anywheresoftware.b4a.objects.collections.Map*/  = _report;
 //BA.debugLineNum = 567;BA.debugLine="StartActivity(ReportDialog)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._reportdialog.getObject()));
 //BA.debugLineNum = 568;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 569;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 11;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 12;BA.debugLine="Public ReportJson As String ' opcional: si se asi";
_reportjson = "";
 //BA.debugLineNum = 13;BA.debugLine="Public AllowEdit As Boolean = False";
_allowedit = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
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
String _vs = "";
String _extrastext = "";
anywheresoftware.b4a.objects.collections.List _obskeys = null;
anywheresoftware.b4a.objects.collections.List _inckeys = null;
Object _vv = null;
String _sval = "";
Object _vv2 = null;
String _sval2 = "";
anywheresoftware.b4a.objects.collections.Map _respmap2 = null;
Object _vv4 = null;
String _detailsresp = "";
String _detailsextras = "";
anywheresoftware.b4a.objects.LabelWrapper _lblresp = null;
int _hresp = 0;
int _hextras = 0;
anywheresoftware.b4a.objects.LabelWrapper _lblextras = null;
int _hcard = 0;
Object _rawobj = null;
String _rawtext = "";
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _jg = null;
anywheresoftware.b4a.objects.LabelWrapper _lblraw = null;
int _hraw = 0;
 //BA.debugLineNum = 251;BA.debugLine="Sub RenderReport(rp As Map)";
 //BA.debugLineNum = 252;BA.debugLine="Dim y As Int = 10dip";
_y = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10));
 //BA.debugLineNum = 253;BA.debugLine="Dim padding As Int = 10dip";
_padding = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10));
 //BA.debugLineNum = 254;BA.debugLine="Dim w As Int = 100%x - 2 * padding";
_w = (int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-2*_padding);
 //BA.debugLineNum = 257;BA.debugLine="If rp.ContainsKey(\"area\") Then";
if (_rp.ContainsKey((Object)("area"))) { 
 //BA.debugLineNum = 258;BA.debugLine="Try";
try { //BA.debugLineNum = 259;BA.debugLine="Dim area As Map = rp.Get(\"area\")";
_area = new anywheresoftware.b4a.objects.collections.Map();
_area = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_rp.Get((Object)("area"))));
 //BA.debugLineNum = 260;BA.debugLine="Dim lblTitle As Label";
_lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 261;BA.debugLine="lblTitle.Initialize(\"lblTitle\")";
_lbltitle.Initialize(mostCurrent.activityBA,"lblTitle");
 //BA.debugLineNum = 262;BA.debugLine="Dim name As String = \"\"";
_name = "";
 //BA.debugLineNum = 263;BA.debugLine="If area.ContainsKey(\"area_name\") Then name = ar";
if (_area.ContainsKey((Object)("area_name"))) { 
_name = BA.ObjectToString(_area.Get((Object)("area_name")));};
 //BA.debugLineNum = 264;BA.debugLine="If name = \"\" And area.ContainsKey(\"areaName\") T";
if ((_name).equals("") && _area.ContainsKey((Object)("areaName"))) { 
_name = BA.ObjectToString(_area.Get((Object)("areaName")));};
 //BA.debugLineNum = 265;BA.debugLine="lblTitle.Text = \"Área: \" & name";
_lbltitle.setText(BA.ObjectToCharSequence("Área: "+_name));
 //BA.debugLineNum = 266;BA.debugLine="lblTitle.TextSize = 16";
_lbltitle.setTextSize((float) (16));
 //BA.debugLineNum = 267;BA.debugLine="lblTitle.Typeface = Typeface.DEFAULT_BOLD";
_lbltitle.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 268;BA.debugLine="Dim hTitle As Int = EstimateLabelHeight(lblTitl";
_htitle = _estimatelabelheight(_lbltitle.getText(),(int) (_lbltitle.getTextSize()),_w);
 //BA.debugLineNum = 269;BA.debugLine="pnl.AddView(lblTitle, padding, y, w, hTitle)";
mostCurrent._pnl.AddView((android.view.View)(_lbltitle.getObject()),_padding,_y,_w,_htitle);
 //BA.debugLineNum = 270;BA.debugLine="y = y + hTitle + 6dip";
_y = (int) (_y+_htitle+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 //BA.debugLineNum = 272;BA.debugLine="Dim desc As String = \"\"";
_desc = "";
 //BA.debugLineNum = 273;BA.debugLine="If area.ContainsKey(\"area_description\") Then de";
if (_area.ContainsKey((Object)("area_description"))) { 
_desc = BA.ObjectToString(_area.Get((Object)("area_description")));};
 //BA.debugLineNum = 274;BA.debugLine="If desc = \"\" And area.ContainsKey(\"areaDescript";
if ((_desc).equals("") && _area.ContainsKey((Object)("areaDescription"))) { 
_desc = BA.ObjectToString(_area.Get((Object)("areaDescription")));};
 //BA.debugLineNum = 275;BA.debugLine="If desc <> \"\" Then";
if ((_desc).equals("") == false) { 
 //BA.debugLineNum = 276;BA.debugLine="Dim lblDesc As Label";
_lbldesc = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 277;BA.debugLine="lblDesc.Initialize(\"lblDesc\")";
_lbldesc.Initialize(mostCurrent.activityBA,"lblDesc");
 //BA.debugLineNum = 278;BA.debugLine="lblDesc.Text = \"Descripción: \" & desc";
_lbldesc.setText(BA.ObjectToCharSequence("Descripción: "+_desc));
 //BA.debugLineNum = 279;BA.debugLine="lblDesc.TextSize = 14";
_lbldesc.setTextSize((float) (14));
 //BA.debugLineNum = 280;BA.debugLine="Dim hDesc As Int = EstimateLabelHeight(lblDesc";
_hdesc = _estimatelabelheight(_lbldesc.getText(),(int) (_lbldesc.getTextSize()),_w);
 //BA.debugLineNum = 281;BA.debugLine="pnl.AddView(lblDesc, padding, y, w, hDesc)";
mostCurrent._pnl.AddView((android.view.View)(_lbldesc.getObject()),_padding,_y,_w,_hdesc);
 //BA.debugLineNum = 282;BA.debugLine="y = y + hDesc + 6dip";
_y = (int) (_y+_hdesc+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 };
 } 
       catch (Exception e31) {
			processBA.setLastException(e31); //BA.debugLineNum = 285;BA.debugLine="Log(\"Error leyendo campo area: \" & LastExceptio";
anywheresoftware.b4a.keywords.Common.LogImpl("32555938","Error leyendo campo area: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 290;BA.debugLine="If rp.ContainsKey(\"car_reports\") Then";
if (_rp.ContainsKey((Object)("car_reports"))) { 
 //BA.debugLineNum = 291;BA.debugLine="Try";
try { //BA.debugLineNum = 292;BA.debugLine="Dim cars As List = rp.Get(\"car_reports\")";
_cars = new anywheresoftware.b4a.objects.collections.List();
_cars = (anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(_rp.Get((Object)("car_reports"))));
 //BA.debugLineNum = 293;BA.debugLine="For i = 0 To cars.Size - 1";
{
final int step37 = 1;
final int limit37 = (int) (_cars.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit37 ;_i = _i + step37 ) {
 //BA.debugLineNum = 294;BA.debugLine="Dim c As Map = cars.Get(i)";
_c = new anywheresoftware.b4a.objects.collections.Map();
_c = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_cars.Get(_i)));
 //BA.debugLineNum = 295;BA.debugLine="Dim carName As String = \"\"";
_carname = "";
 //BA.debugLineNum = 296;BA.debugLine="If c.ContainsKey(\"car_name\") Then carName = c.";
if (_c.ContainsKey((Object)("car_name"))) { 
_carname = BA.ObjectToString(_c.Get((Object)("car_name")));};
 //BA.debugLineNum = 299;BA.debugLine="Dim card As Panel";
_card = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 300;BA.debugLine="card.Initialize(\"card\" & i)";
_card.Initialize(mostCurrent.activityBA,"card"+BA.NumberToString(_i));
 //BA.debugLineNum = 301;BA.debugLine="card.Color = Colors.ARGB(255, 245, 245, 245)";
_card.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (245),(int) (245),(int) (245)));
 //BA.debugLineNum = 302;BA.debugLine="pnl.AddView(card, padding, y, w, 60dip) ' altu";
mostCurrent._pnl.AddView((android.view.View)(_card.getObject()),_padding,_y,_w,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 305;BA.debugLine="Dim lblCar As Label";
_lblcar = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 306;BA.debugLine="lblCar.Initialize(\"lblCar\" & i)";
_lblcar.Initialize(mostCurrent.activityBA,"lblCar"+BA.NumberToString(_i));
 //BA.debugLineNum = 307;BA.debugLine="lblCar.Text = \"CAR: \" & carName";
_lblcar.setText(BA.ObjectToCharSequence("CAR: "+_carname));
 //BA.debugLineNum = 308;BA.debugLine="lblCar.TextSize = 15";
_lblcar.setTextSize((float) (15));
 //BA.debugLineNum = 309;BA.debugLine="lblCar.Typeface = Typeface.DEFAULT_BOLD";
_lblcar.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 310;BA.debugLine="Dim hCarName As Int = EstimateLabelHeight(lblC";
_hcarname = _estimatelabelheight(_lblcar.getText(),(int) (_lblcar.getTextSize()),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))));
 //BA.debugLineNum = 311;BA.debugLine="card.AddView(lblCar, 8dip, 4dip, w - 16dip, hC";
_card.AddView((android.view.View)(_lblcar.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4)),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))),_hcarname);
 //BA.debugLineNum = 314;BA.debugLine="Dim responsesText As String = \"\"";
_responsestext = "";
 //BA.debugLineNum = 315;BA.debugLine="If c.ContainsKey(\"responses\") Then";
if (_c.ContainsKey((Object)("responses"))) { 
 //BA.debugLineNum = 316;BA.debugLine="Try";
try { //BA.debugLineNum = 317;BA.debugLine="Dim respMap As Map = c.Get(\"responses\")";
_respmap = new anywheresoftware.b4a.objects.collections.Map();
_respmap = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_c.Get((Object)("responses"))));
 //BA.debugLineNum = 318;BA.debugLine="For Each k As String In respMap.Keys";
{
final anywheresoftware.b4a.BA.IterableList group56 = _respmap.Keys();
final int groupLen56 = group56.getSize()
;int index56 = 0;
;
for (; index56 < groupLen56;index56++){
_k = BA.ObjectToString(group56.Get(index56));
 //BA.debugLineNum = 319;BA.debugLine="Try";
try { //BA.debugLineNum = 320;BA.debugLine="Dim v As Object = respMap.Get(k)";
_v = _respmap.Get((Object)(_k));
 //BA.debugLineNum = 321;BA.debugLine="Dim vs As String = FormatValue(v)";
_vs = _formatvalue(_v);
 //BA.debugLineNum = 322;BA.debugLine="responsesText = responsesText & k & \": \" &";
_responsestext = _responsestext+_k+": "+_vs+anywheresoftware.b4a.keywords.Common.CRLF;
 } 
       catch (Exception e62) {
			processBA.setLastException(e62); //BA.debugLineNum = 324;BA.debugLine="Log(\"RenderReport: error leyendo responses";
anywheresoftware.b4a.keywords.Common.LogImpl("32555977","RenderReport: error leyendo responses key '"+_k+"': "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 }
};
 } 
       catch (Exception e66) {
			processBA.setLastException(e66); //BA.debugLineNum = 328;BA.debugLine="responsesText = \"Error leyendo responses\"";
_responsestext = "Error leyendo responses";
 //BA.debugLineNum = 329;BA.debugLine="Log(\"Error reading responses map: \" & LastEx";
anywheresoftware.b4a.keywords.Common.LogImpl("32555982","Error reading responses map: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 334;BA.debugLine="Dim extrasText As String = \"\"";
_extrastext = "";
 //BA.debugLineNum = 337;BA.debugLine="Dim obsKeys As List";
_obskeys = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 338;BA.debugLine="obsKeys.Initialize";
_obskeys.Initialize();
 //BA.debugLineNum = 339;BA.debugLine="obsKeys.AddAll(Array As String(\"observacion\",\"";
_obskeys.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"observacion","observación","Observacion","Observación","observaciones","Observaciones","obs"}));
 //BA.debugLineNum = 341;BA.debugLine="Dim incKeys As List";
_inckeys = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 342;BA.debugLine="incKeys.Initialize";
_inckeys.Initialize();
 //BA.debugLineNum = 343;BA.debugLine="incKeys.AddAll(Array As String(\"incidencia\",\"I";
_inckeys.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"incidencia","Incidencia","incidencias","Incidencias","incid","incidencias_text"}));
 //BA.debugLineNum = 346;BA.debugLine="For Each k As String In obsKeys";
{
final anywheresoftware.b4a.BA.IterableList group77 = _obskeys;
final int groupLen77 = group77.getSize()
;int index77 = 0;
;
for (; index77 < groupLen77;index77++){
_k = BA.ObjectToString(group77.Get(index77));
 //BA.debugLineNum = 347;BA.debugLine="If c.ContainsKey(k) Then";
if (_c.ContainsKey((Object)(_k))) { 
 //BA.debugLineNum = 348;BA.debugLine="Try";
try { //BA.debugLineNum = 349;BA.debugLine="Dim vv As Object = c.Get(k)";
_vv = _c.Get((Object)(_k));
 //BA.debugLineNum = 350;BA.debugLine="Dim sVal As String = FormatValue(vv)";
_sval = _formatvalue(_vv);
 //BA.debugLineNum = 351;BA.debugLine="If sVal <> \"\" Then";
if ((_sval).equals("") == false) { 
 //BA.debugLineNum = 352;BA.debugLine="extrasText = extrasText & \"Observación: \"";
_extrastext = _extrastext+"Observación: "+_sval+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 353;BA.debugLine="Log(\"RenderReport: found observacion key '";
anywheresoftware.b4a.keywords.Common.LogImpl("32556006","RenderReport: found observacion key '"+_k+"': "+_sval.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(200,_sval.length()))),0);
 //BA.debugLineNum = 354;BA.debugLine="Exit";
if (true) break;
 };
 } 
       catch (Exception e88) {
			processBA.setLastException(e88); //BA.debugLineNum = 357;BA.debugLine="Log(\"RenderReport: error leyendo observacio";
anywheresoftware.b4a.keywords.Common.LogImpl("32556010","RenderReport: error leyendo observacion key '"+_k+"': "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 }
};
 //BA.debugLineNum = 363;BA.debugLine="For Each k As String In incKeys";
{
final anywheresoftware.b4a.BA.IterableList group92 = _inckeys;
final int groupLen92 = group92.getSize()
;int index92 = 0;
;
for (; index92 < groupLen92;index92++){
_k = BA.ObjectToString(group92.Get(index92));
 //BA.debugLineNum = 364;BA.debugLine="If c.ContainsKey(k) Then";
if (_c.ContainsKey((Object)(_k))) { 
 //BA.debugLineNum = 365;BA.debugLine="Try";
try { //BA.debugLineNum = 366;BA.debugLine="Dim vv2 As Object = c.Get(k)";
_vv2 = _c.Get((Object)(_k));
 //BA.debugLineNum = 367;BA.debugLine="Dim sVal2 As String = FormatValue(vv2)";
_sval2 = _formatvalue(_vv2);
 //BA.debugLineNum = 368;BA.debugLine="If sVal2 <> \"\" Then";
if ((_sval2).equals("") == false) { 
 //BA.debugLineNum = 369;BA.debugLine="extrasText = extrasText & \"Incidencia: \" &";
_extrastext = _extrastext+"Incidencia: "+_sval2+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 370;BA.debugLine="Log(\"RenderReport: found incidencia key '\"";
anywheresoftware.b4a.keywords.Common.LogImpl("32556023","RenderReport: found incidencia key '"+_k+"': "+_sval2.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(200,_sval2.length()))),0);
 //BA.debugLineNum = 371;BA.debugLine="Exit";
if (true) break;
 };
 } 
       catch (Exception e103) {
			processBA.setLastException(e103); //BA.debugLineNum = 374;BA.debugLine="Log(\"RenderReport: error leyendo incidencia";
anywheresoftware.b4a.keywords.Common.LogImpl("32556027","RenderReport: error leyendo incidencia key '"+_k+"': "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 }
};
 //BA.debugLineNum = 380;BA.debugLine="If extrasText = \"\" And c.ContainsKey(\"response";
if ((_extrastext).equals("") && _c.ContainsKey((Object)("responses"))) { 
 //BA.debugLineNum = 381;BA.debugLine="Try";
try { //BA.debugLineNum = 382;BA.debugLine="Dim respMap2 As Map = c.Get(\"responses\")";
_respmap2 = new anywheresoftware.b4a.objects.collections.Map();
_respmap2 = (anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_c.Get((Object)("responses"))));
 //BA.debugLineNum = 384;BA.debugLine="For Each k As String In obsKeys";
{
final anywheresoftware.b4a.BA.IterableList group110 = _obskeys;
final int groupLen110 = group110.getSize()
;int index110 = 0;
;
for (; index110 < groupLen110;index110++){
_k = BA.ObjectToString(group110.Get(index110));
 //BA.debugLineNum = 385;BA.debugLine="If respMap2.ContainsKey(k) Then";
if (_respmap2.ContainsKey((Object)(_k))) { 
 //BA.debugLineNum = 386;BA.debugLine="Try";
try { //BA.debugLineNum = 387;BA.debugLine="Dim vv As Object = respMap2.Get(k)";
_vv = _respmap2.Get((Object)(_k));
 //BA.debugLineNum = 388;BA.debugLine="Dim sVal As String = FormatValue(vv)";
_sval = _formatvalue(_vv);
 //BA.debugLineNum = 389;BA.debugLine="extrasText = extrasText & \"Observación: \"";
_extrastext = _extrastext+"Observación: "+_sval+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 390;BA.debugLine="Log(\"RenderReport: found observacion insi";
anywheresoftware.b4a.keywords.Common.LogImpl("32556043","RenderReport: found observacion inside responses key '"+_k+"': "+_sval.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(200,_sval.length()))),0);
 //BA.debugLineNum = 391;BA.debugLine="Exit";
if (true) break;
 } 
       catch (Exception e119) {
			processBA.setLastException(e119); //BA.debugLineNum = 393;BA.debugLine="Log(\"RenderReport: error leyendo observac";
anywheresoftware.b4a.keywords.Common.LogImpl("32556046","RenderReport: error leyendo observacion inside responses key '"+_k+"': "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 }
};
 //BA.debugLineNum = 398;BA.debugLine="If extrasText = \"\" Then";
if ((_extrastext).equals("")) { 
 //BA.debugLineNum = 399;BA.debugLine="For Each k As String In incKeys";
{
final anywheresoftware.b4a.BA.IterableList group124 = _inckeys;
final int groupLen124 = group124.getSize()
;int index124 = 0;
;
for (; index124 < groupLen124;index124++){
_k = BA.ObjectToString(group124.Get(index124));
 //BA.debugLineNum = 400;BA.debugLine="If respMap2.ContainsKey(k) Then";
if (_respmap2.ContainsKey((Object)(_k))) { 
 //BA.debugLineNum = 401;BA.debugLine="Try";
try { //BA.debugLineNum = 402;BA.debugLine="Dim vv4 As Object = respMap2.Get(k)";
_vv4 = _respmap2.Get((Object)(_k));
 //BA.debugLineNum = 403;BA.debugLine="Dim sVal2 As String = FormatValue(vv4)";
_sval2 = _formatvalue(_vv4);
 //BA.debugLineNum = 404;BA.debugLine="extrasText = extrasText & \"Incidencia: \"";
_extrastext = _extrastext+"Incidencia: "+_sval2+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 405;BA.debugLine="Log(\"RenderReport: found incidencia insi";
anywheresoftware.b4a.keywords.Common.LogImpl("32556058","RenderReport: found incidencia inside responses key '"+_k+"': "+_sval2.substring((int) (0),(int) (anywheresoftware.b4a.keywords.Common.Min(200,_sval2.length()))),0);
 //BA.debugLineNum = 406;BA.debugLine="Exit";
if (true) break;
 } 
       catch (Exception e133) {
			processBA.setLastException(e133); //BA.debugLineNum = 408;BA.debugLine="Log(\"RenderReport: error leyendo inciden";
anywheresoftware.b4a.keywords.Common.LogImpl("32556061","RenderReport: error leyendo incidencia inside responses key '"+_k+"': "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 }
};
 };
 } 
       catch (Exception e139) {
			processBA.setLastException(e139); //BA.debugLineNum = 414;BA.debugLine="Log(\"RenderReport: error buscando observ/inc";
anywheresoftware.b4a.keywords.Common.LogImpl("32556067","RenderReport: error buscando observ/incid en responses: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 //BA.debugLineNum = 419;BA.debugLine="Dim detailsResp As String = responsesText";
_detailsresp = _responsestext;
 //BA.debugLineNum = 420;BA.debugLine="Dim detailsExtras As String = extrasText";
_detailsextras = _extrastext;
 //BA.debugLineNum = 422;BA.debugLine="If detailsResp = \"\" Then detailsResp = \"(sin r";
if ((_detailsresp).equals("")) { 
_detailsresp = "(sin respuestas)";};
 //BA.debugLineNum = 423;BA.debugLine="If detailsExtras = \"\" Then detailsExtras = \"\"";
if ((_detailsextras).equals("")) { 
_detailsextras = "";};
 //BA.debugLineNum = 426;BA.debugLine="Dim lblResp As Label";
_lblresp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 427;BA.debugLine="lblResp.Initialize(\"lblResp\" & i)";
_lblresp.Initialize(mostCurrent.activityBA,"lblResp"+BA.NumberToString(_i));
 //BA.debugLineNum = 428;BA.debugLine="lblResp.Text = detailsResp";
_lblresp.setText(BA.ObjectToCharSequence(_detailsresp));
 //BA.debugLineNum = 429;BA.debugLine="lblResp.TextSize = 13";
_lblresp.setTextSize((float) (13));
 //BA.debugLineNum = 430;BA.debugLine="Dim hResp As Int = EstimateLabelHeight(lblResp";
_hresp = _estimatelabelheight(_lblresp.getText(),(int) (_lblresp.getTextSize()),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))));
 //BA.debugLineNum = 431;BA.debugLine="card.AddView(lblResp, 8dip, 4dip + hCarName, w";
_card.AddView((android.view.View)(_lblresp.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))+_hcarname),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))),_hresp);
 //BA.debugLineNum = 433;BA.debugLine="Dim hExtras As Int = 0";
_hextras = (int) (0);
 //BA.debugLineNum = 434;BA.debugLine="If detailsExtras <> \"\" Then";
if ((_detailsextras).equals("") == false) { 
 //BA.debugLineNum = 435;BA.debugLine="Dim lblExtras As Label";
_lblextras = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 436;BA.debugLine="lblExtras.Initialize(\"lblExtras\" & i)";
_lblextras.Initialize(mostCurrent.activityBA,"lblExtras"+BA.NumberToString(_i));
 //BA.debugLineNum = 437;BA.debugLine="lblExtras.Text = detailsExtras";
_lblextras.setText(BA.ObjectToCharSequence(_detailsextras));
 //BA.debugLineNum = 438;BA.debugLine="lblExtras.TextSize = 13";
_lblextras.setTextSize((float) (13));
 //BA.debugLineNum = 439;BA.debugLine="hExtras = EstimateLabelHeight(lblExtras.Text,";
_hextras = _estimatelabelheight(_lblextras.getText(),(int) (_lblextras.getTextSize()),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))));
 //BA.debugLineNum = 440;BA.debugLine="card.AddView(lblExtras, 8dip, 4dip + hCarName";
_card.AddView((android.view.View)(_lblextras.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (4))+_hcarname+_hresp),(int) (_w-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16))),_hextras);
 };
 //BA.debugLineNum = 444;BA.debugLine="Dim hCard As Int = hCarName + hResp + hExtras";
_hcard = (int) (_hcarname+_hresp+_hextras+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16)));
 //BA.debugLineNum = 445;BA.debugLine="card.SetLayout(card.Left, card.Top, card.Width";
_card.SetLayout(_card.getLeft(),_card.getTop(),_card.getWidth(),_hcard);
 //BA.debugLineNum = 446;BA.debugLine="y = y + hCard + 8dip";
_y = (int) (_y+_hcard+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8)));
 }
};
 } 
       catch (Exception e166) {
			processBA.setLastException(e166); //BA.debugLineNum = 449;BA.debugLine="Log(\"Error leyendo car_reports: \" & LastExcepti";
anywheresoftware.b4a.keywords.Common.LogImpl("32556102","Error leyendo car_reports: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 }else {
 //BA.debugLineNum = 453;BA.debugLine="If rp.ContainsKey(\"raw\") Then";
if (_rp.ContainsKey((Object)("raw"))) { 
 //BA.debugLineNum = 454;BA.debugLine="Try";
try { //BA.debugLineNum = 455;BA.debugLine="Dim rawObj As Object = rp.Get(\"raw\")";
_rawobj = _rp.Get((Object)("raw"));
 //BA.debugLineNum = 456;BA.debugLine="Dim rawText As String = \"\"";
_rawtext = "";
 //BA.debugLineNum = 457;BA.debugLine="If rawObj Is Map Then";
if (_rawobj instanceof java.util.Map) { 
 //BA.debugLineNum = 458;BA.debugLine="Dim jg As JSONGenerator";
_jg = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 459;BA.debugLine="jg.Initialize(rawObj)";
_jg.Initialize((anywheresoftware.b4a.objects.collections.Map) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.Map(), (java.util.Map)(_rawobj)));
 //BA.debugLineNum = 460;BA.debugLine="rawText = jg.ToString";
_rawtext = _jg.ToString();
 }else {
 //BA.debugLineNum = 462;BA.debugLine="rawText = rawObj";
_rawtext = BA.ObjectToString(_rawobj);
 };
 //BA.debugLineNum = 464;BA.debugLine="Dim lblRaw As Label";
_lblraw = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 465;BA.debugLine="lblRaw.Initialize(\"lblRaw\")";
_lblraw.Initialize(mostCurrent.activityBA,"lblRaw");
 //BA.debugLineNum = 466;BA.debugLine="lblRaw.Text = rawText";
_lblraw.setText(BA.ObjectToCharSequence(_rawtext));
 //BA.debugLineNum = 467;BA.debugLine="lblRaw.TextSize = 13";
_lblraw.setTextSize((float) (13));
 //BA.debugLineNum = 468;BA.debugLine="Dim hRaw As Int = EstimateLabelHeight(lblRaw.T";
_hraw = _estimatelabelheight(_lblraw.getText(),(int) (_lblraw.getTextSize()),_w);
 //BA.debugLineNum = 469;BA.debugLine="pnl.AddView(lblRaw, 10dip, y, w, hRaw)";
mostCurrent._pnl.AddView((android.view.View)(_lblraw.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),_y,_w,_hraw);
 //BA.debugLineNum = 470;BA.debugLine="y = y + hRaw + 6dip";
_y = (int) (_y+_hraw+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (6)));
 } 
       catch (Exception e188) {
			processBA.setLastException(e188); //BA.debugLineNum = 472;BA.debugLine="Log(\"Error mostrando raw: \" & LastException.Me";
anywheresoftware.b4a.keywords.Common.LogImpl("32556125","Error mostrando raw: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),0);
 };
 };
 };
 //BA.debugLineNum = 477;BA.debugLine="pnl.Height = y + 20dip";
mostCurrent._pnl.setHeight((int) (_y+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))));
 //BA.debugLineNum = 480;BA.debugLine="If AllowEdit Then";
if (_allowedit) { 
 //BA.debugLineNum = 481;BA.debugLine="AddActionButtons(y + 30dip)";
_addactionbuttons((int) (_y+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30))));
 };
 //BA.debugLineNum = 483;BA.debugLine="End Sub";
return "";
}
}
