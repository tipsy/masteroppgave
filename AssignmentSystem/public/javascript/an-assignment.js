$(document).ready(function () {

    $(".ace-editor-instance").each(function(){
        initEditor( $(this).attr("id") );
    });

    function initEditor(editorID){
        var editorID = ace.edit(editorID);
        editorID.setTheme("ace/theme/eclipse");
        editorID.getSession().setMode("ace/mode/java");
    }

});