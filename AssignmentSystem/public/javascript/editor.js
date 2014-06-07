$(document).ready(function () {

    var editors = initEditors();
    var webSocket = openNewWebSocket();
    var annotationList = createAnnotationList(getAnnotationData());
    editors[0].getSession().setAnnotations(annotationList);

    console.log("Current route is: "+jsRoutes.controllers.AssignmentController.openEditorSocket(getCurrentProblemID()).webSocketURL());

    webSocket.onopen = function()  { console.log('ws connected'); };
    webSocket.onerror = function() { console.log('ws errrrrror'); };
    webSocket.onclose = function() { console.log('ws cloooosed'); };
    webSocket.onmessage = function(msgevent) {
        var msg = msgevent.data;
        console.log(msg);
        webSocket.send(editors[0].getSession().getValue());
    };

});

function initEditors() {
    var editors = [];
    $(".ace-editor-instance").each(function () {
        editors.push(createEditor($(this).attr("id")));
    });
    return editors;
}

function createEditor(editorID){
    var editor = ace.edit(editorID);
    editor.setTheme("ace/theme/eclipse");
    editor.getSession().setMode("ace/mode/java");
    return editor;
}

function openNewWebSocket() {
    return new WebSocket(jsRoutes.controllers.AssignmentController.openEditorSocket(getCurrentProblemID()).webSocketURL());
}

function getCurrentProblemID(){
    return $("#problem-id").data("problemid");
}

function createAnnotationList(data) {
    var annotationList = data.problems.map(function (problem) {
        return new Annotation(problem.lineNumber, problem.message, problem.type);
    });
    return annotationList;
}

function getAnnotationData() {
    var data = {
        "problems": [
            {"lineNumber": 1, "message": "Something is wrong", "type": "error"},
            {"lineNumber": 2, "message": "Something else is wrong too", "type": "warning"},
            {"lineNumber": 3, "message": "THIS. IS. INFORMATION", "type": "info"}
        ]
    }
    return data;
}

function Annotation(lineNumber, message, type){
    this.row = lineNumber;
    this.text = message;
    this.type = type; // "error", "warning", "info"
}