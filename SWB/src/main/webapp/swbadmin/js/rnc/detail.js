function add(url, id) {
    dojo.xhrPost({
        url: url + id,
        load: function (data) {
            dojo.byId('addCollection').innerHTML = data;
            $('#addCollection').modal('show');
        }
    });
}
function loadDoc(url, id) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            jQuery("#addCollection-tree").html(this.responseText);
            $("#addCollection").dialog("open");
        } else if (this.readyState == 4 && this.status == 403) {
            jQuery("#dialog-message-tree").text("Regístrate o inicia sesión para crear tus colecciones.");
            $("#dialog-message-tree").dialog("open");
        }
    };
    xhttp.open("POST", url + id, true);
    xhttp.send();
}
function nextObj(url, iEntry, iDigit) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            jQuery("#idetail").html(this.responseText);
        } else if (this.readyState == 4 && this.status == 403) {
            console.log(this.responseText);
        }
    };
    xhttp.open("POST", url + iEntry + '&n=' + iDigit, true);
    xhttp.send();
}
function dismiss() {
    $("#addCollection").dialog("close");
}
function addnew(uri) {
    dismiss();
    dojo.xhrPost({
        url: uri,
        load: function (data) {
            dojo.byId('newCollection').innerHTML = data;
            $('#newCollection').modal('show');
        }
    });
}