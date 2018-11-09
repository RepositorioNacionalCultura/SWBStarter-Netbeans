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
    Amplitude.pause();
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

function nextResult(url) {
    window.location.replace(url);
}

function vistaMosaico() {
    document.getElementById("resultados").classList.remove("lista");
    document.getElementById("vistaMosaico").classList.add("select");
    document.getElementById("vistaLista").classList.remove("select");
    var x = document.getElementsByClassName("pieza-res-img");
    var y = document.getElementsByClassName("pieza-res-inf");
    var i;
    for (i = 0; i < x.length; i++) {
        x[i].classList.remove("lista");
        y[i].classList.remove("lista");
        y[i].style.width = "";
    }
}
function vistaLista() {
    document.getElementById("resultados").classList.add("lista");
    document.getElementById("vistaLista").classList.add("select");
    document.getElementById("vistaMosaico").classList.remove("select");
    var x = document.getElementsByClassName("pieza-res-img");
    var y = document.getElementsByClassName("pieza-res-inf");
    var i;
    for (i = 0; i < x.length; i++) {
        x[i].classList.add("lista");
        y[i].classList.add("lista");
    }
}

function go2Collection(id, url) {
    var entry = url + "?id="+id;
    $( "#dialog-go-tree").dialog({
        autoOpen: false,
        dialogClass: 'msg-dialog',
        buttons: [
            {
                "class": 'btn btn-sm btn-rojo',
                text: "Cerrar",
                click: function() {
                    $( this ).dialog( "close" );
                }
            },
            {
                "class": 'btn btn-sm btn-rojo',
                text: "Ir a colecci\xF3n",
                click: function() {
                    $( this ).dialog( "close" );
                    location.replace(entry)
                }
            }
        ]
    });
    $("#dialog-go-tree" ).dialog( "open" );
}

function share(op, status) {
    if (status == 'false') {
        if (op == 'fb') {
            jQuery("#dialog-share").text("La colección debe ser pública para poder compartir en facebook.");
        } else {
            jQuery("#dialog-share").text("La colección debe ser pública para poder compartir en twitter.");
        }
        $('#alertShare').modal('show');
    } else {
        if (op == 'fb') {
            fbShare();
        } else {
            window.open(url2Share, '', 'width=500,height=500');
        }
    }
}