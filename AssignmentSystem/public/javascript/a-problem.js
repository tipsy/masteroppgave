$(document).ready(function () {

    $(".ace-editor-instance").each(function(){
        initEditor( $(this).attr("id") );
    });

    $("#ae-toggle-fullscreen").click(function(){
        $("#ace-editor-wrapper").hasClass("maximized") ? leaveFullScreen() : goFullScreen();
        $("#ace-editor-wrapper").toggleClass("maximized");
        $("#ae-toggle-fullscreen").toggleClass("fa-expand fa-compress");
    });

});


function initEditor(editorID){
    var editorID = ace.edit(editorID);
    editorID.setTheme("ace/theme/eclipse");
    editorID.getSession().setMode("ace/mode/java");
}

function goFullScreen(){
    var docElement, request;
    docElement = document.documentElement;
    request = docElement.requestFullScreen || docElement.webkitRequestFullScreen || docElement.mozRequestFullScreen || docElement.msRequestFullScreen;
    if(typeof request!="undefined" && request){
        request.call(docElement);
    }
}

function leaveFullScreen(){
    var docElement, request;
    docElement = document;
    request = docElement.cancelFullScreen|| docElement.webkitCancelFullScreen || docElement.mozCancelFullScreen || docElement.msCancelFullScreen || docElement.exitFullscreen;
    if(typeof request!="undefined" && request){
        request.call(docElement);
    }
}