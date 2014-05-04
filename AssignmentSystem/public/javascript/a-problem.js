$(document).ready(function () {

    $(".ace-editor-instance").each(function(){
        initEditor( $(this).attr("id") );
    });

    $("#ae-toggle-fullscreen").click(function(){
        $("#ace-editor-wrapper").toggleClass("maximized");
        $("#ae-toggle-fullscreen").toggleClass("fa-expand fa-compress");
    });

});


function initEditor(editorID){
    var editorID = ace.edit(editorID);
    editorID.setTheme("ace/theme/eclipse");
    editorID.getSession().setMode("ace/mode/java");
}