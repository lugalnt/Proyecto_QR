package b4a.example;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.B4AClass;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class b4xmainpage extends B4AClass.ImplB4AClass implements BA.SubDelegator{
    private static java.util.HashMap<String, java.lang.reflect.Method> htSubs;
    private void innerInitialize(BA _ba) throws Exception {
        if (ba == null) {
            ba = new BA(_ba, this, htSubs, "b4a.example.b4xmainpage");
            if (htSubs == null) {
                ba.loadHtSubs(this.getClass());
                htSubs = ba.htSubs;
            }
            
        }
        if (BA.isShellModeRuntimeCheck(ba)) 
			   this.getClass().getMethod("_class_globals", b4a.example.b4xmainpage.class).invoke(this, new Object[] {null});
        else
            ba.raiseEvent2(null, true, "class_globals", false);
    }

 public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.B4XViewWrapper _root = null;
public anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public b4a.example.googlecodescanner _scanner = null;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.b4xpages _b4xpages = null;
public b4a.example.b4xcollections _b4xcollections = null;
public String  _b4xpage_created(anywheresoftware.b4a.objects.B4XViewWrapper _root1) throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Private Sub B4XPage_Created (Root1 As B4XView)";
 //BA.debugLineNum = 20;BA.debugLine="Root = Root1";
_root = _root1;
 //BA.debugLineNum = 21;BA.debugLine="Root.LoadLayout(\"MainPage\")";
_root.LoadLayout("MainPage",ba);
 //BA.debugLineNum = 22;BA.debugLine="Scanner.Initialize";
_scanner._initialize /*String*/ (ba);
 //BA.debugLineNum = 23;BA.debugLine="End Sub";
return "";
}
public void  _button1_click() throws Exception{
ResumableSub_Button1_Click rsub = new ResumableSub_Button1_Click(this);
rsub.resume(ba, null);
}
public static class ResumableSub_Button1_Click extends BA.ResumableSub {
public ResumableSub_Button1_Click(b4a.example.b4xmainpage parent) {
this.parent = parent;
}
b4a.example.b4xmainpage parent;
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
 //BA.debugLineNum = 26;BA.debugLine="Dim formats As List = Array(Scanner.FORMAT_ALL_FO";
_formats = new anywheresoftware.b4a.objects.collections.List();
_formats = anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent._scanner._format_all_formats /*int*/ )});
 //BA.debugLineNum = 27;BA.debugLine="Wait For (Scanner.Scan(formats)) Complete (Result";
parent.__c.WaitFor("complete", ba, this, parent._scanner._scan /*anywheresoftware.b4a.keywords.Common.ResumableSubWrapper*/ (_formats));
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (b4a.example.googlecodescanner._scannerresult) result[0];
;
 //BA.debugLineNum = 28;BA.debugLine="If Result.Success Then";
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
 //BA.debugLineNum = 29;BA.debugLine="Msgbox(Result.Value,\"Escaneado\")";
parent.__c.Msgbox(BA.ObjectToCharSequence(_result.Value /*String*/ ),BA.ObjectToCharSequence("Escaneado"),ba);
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public void  _complete(b4a.example.googlecodescanner._scannerresult _result) throws Exception{
}
public String  _class_globals() throws Exception{
 //BA.debugLineNum = 8;BA.debugLine="Sub Class_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Private Root As B4XView";
_root = new anywheresoftware.b4a.objects.B4XViewWrapper();
 //BA.debugLineNum = 10;BA.debugLine="Private xui As XUI";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 11;BA.debugLine="Private Scanner As GoogleCodeScanner";
_scanner = new b4a.example.googlecodescanner();
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public String  _initialize(anywheresoftware.b4a.BA _ba) throws Exception{
innerInitialize(_ba);
 //BA.debugLineNum = 14;BA.debugLine="Public Sub Initialize";
 //BA.debugLineNum = 16;BA.debugLine="End Sub";
return "";
}
public Object callSub(String sub, Object sender, Object[] args) throws Exception {
BA.senderHolder.set(sender);
if (BA.fastSubCompare(sub, "B4XPAGE_CREATED"))
	return _b4xpage_created((anywheresoftware.b4a.objects.B4XViewWrapper) args[0]);
return BA.SubDelegator.SubNotFound;
}
}
