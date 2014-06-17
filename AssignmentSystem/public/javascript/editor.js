$(document).ready(function () {

    var editors = initEditors();
    var webSocket = openNewWebSocket();
    initCollapsibleHeaders();

    //placeholders
    var annotationList = createAnnotationList(getAnnotationData());
    editors[0].getSession().setAnnotations(annotationList);

    webSocket.onopen = function() {
        console.log('ws connected ('+jsRoutes.controllers.AssignmentController.openEditorSocket(getCurrentProblemID()).webSocketURL()+')');
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
        if (object.type === 'runMainResult') {
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

                sendMessage("updateSourceCode", {
                    fileId: fileId,
                    sourceCode: sourceCode
                });
            }, 300);
        });
    });

    function sendMessage(type, message) {
        message = message || {};
        var data = JSON.stringify({type: type, data: message});

        webSocket.send(data);
        console.log("Sent: " + data);
    }

    /*
        Click listeners go below here
        vvvvvvvvvvvvvvvvvvvvvvvvvvvvv
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
        sendMessage("runMain");
    });

    $("#run-tests-button").click(function(){
        sendMessage("runTests");
    });

    $("#deliver-assignment-button").click(function(){
        sendMessage("deliverAssignment");
    });

    /*
        Helper functions go below here
        vvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
    */

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

        //init auto-complete
        editor.setOptions({enableBasicAutocompletion: true});
        initAutoRhyme();
        //end init auto-complete

        return editor;
    }

    function initAutoRhyme(){
        var langTools = ace.require("ace/ext/language_tools");
        var rhymeCompleter = {
            getCompletions: function(editor, session, pos, prefix, callback) {
                if (prefix.length === 0) { callback(null, []); return }
                $.getJSON(
                    "http://rhymebrain.com/talk?function=getRhymes&word=" + prefix,
                    function(wordList) {
                        callback(null, wordList.map(function(ea) {
                            return {name: ea.word, value: ea.word, score: ea.score, meta: "rhyme"}
                        }));
                    })
            }
        }
        langTools.addCompleter(rhymeCompleter);
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

});



