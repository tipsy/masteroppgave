$(document).ready(function () {

    var editors = initEditors();
    var webSocket = openNewWebSocket();
    var annotationList = createAnnotationList(getAnnotationData());
    editors[0].getSession().setAnnotations(annotationList);
    initCollapsibleHeaders();

    console.log("Current route is: "+jsRoutes.controllers.AssignmentController.openEditorSocket(getCurrentProblemID()).webSocketURL());

    webSocket.onopen = function() {
        console.log('ws connected');
    };

    webSocket.onerror = function() {
        console.log('ws error');
    };

    webSocket.onclose = function() {
        console.log('ws closed');
    };

    webSocket.onmessage = function(msgevent) {
        var object = JSON.parse(msgevent.data);
        console.log(object);
        if (object.type === 'runCodeResult') {
            $('.ace-editor-console').text(object.data.output);
        }
    };

    $(editors).each(function() {
        var editor = this;
        
        this.on('change', function() {
            throttle(function(){
                //this code is called 400ms after the last change-event

                var fileId = $(editor.container).attr('data-file-id');
                var sourceCode = editor.getSession().getValue();

                sendMessage({
                    "type": "updateSourceCode",
                    "data": {
                        "id": fileId.toString(),
                        "sourceCode": sourceCode
                    }
                });
            }, 400);
        });
    });

    /*
        Click listeners go here
        vvvvvvvvvvvvvvvvvvvvvvv
    */

    $("#ae-toggle-fullscreen").click(function(){
        $(".hidden-when-editor-maximized").toggle();
        $("#ace-editor-wrapper").toggleClass("maximized");
        $("#ae-toggle-fullscreen").toggleClass("fa-expand fa-compress");
        $(editors).each(function() { this.resize(); });
    });

    $(".ae-theme-settings").click(function(){
        var theme = ("ace/theme/"+$(this).attr("id"));
        $(editors).each(function() { this.setTheme(theme); });
    });

    $(".collapsing-header").click(function(){
        $(this).find("i").toggleClass("fa-angle-down fa-angle-right"); //toggle icon on header-click
    });

    $("#run-code-button").click(function(){
        sendMessage({"type": "runCode", "data": {}});
    });

    $("#run-tests-button").click(function(){
        sendMessage({"type": "runTests", "data": {}});
    });

    $("#deliver-assignment-button").click(function(){
        sendMessage({"type": "deliverAssignment", "data": {}});
    });

    function sendMessage(message) {
        webSocket.send(JSON.stringify(message));
        console.log("Sent " + JSON.stringify(message));
    }
});

function initCollapsibleHeaders() {
    $(".hidden-when-editor-maximized").collapsible(); //makes ever header in this div collapsible
}

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

var throttle = (function(){
    var timer = 0;
    return function(callback, ms){
        clearTimeout (timer);
        timer = setTimeout(callback, ms);
    };
})();