<%-- 
    Document   : pbcollections
    Created on : 28/06/2018, 01:21:54 PM
    Author     : sergio.tellez
--%>

<main role="main" class="container-fluid">
    <div class="row resultadosbar">
        <div class="col-md-3 regresar"><a href="#" class="rojo"><span class="ion-arrow-left-c"></span>Regresar</a></div>
        <div class="col-md-9 rutatop"><p><a href="#">Inicio</a> / Coleccion</p></div>
    </div>
    <div class="row offcanvascont">
        <div class="offcanvas rojo-bg">
            <span onclick="openNav()" id="offcanvasAbre">
                <em class="fa fa-sliders" aria-hidden="true"></em> Filtros 
                <i class="ion-chevrlon-right " aria-hidden="true"></i>
            </span>
            <span onclick="closeNav()" id="offcanvasCierra">
                <em class="fa fa-sliders" aria-hidden="true"></em> Filtros 
                <i class="ion-close " aria-hidden="true"></i>
            </span>
        </div>
        <div id="sidebar">
            <div id="accordion" role="tablist">
            </div>
        </div>
        <div id="contenido">         
            <div class="ruta-resultado row">
                <div class="col-12 col-sm-8 col-md-8">
                    <p class="oswL rojo">EXPLORAR EN <span class="oswB rojo">500 000</span> REGISTROS EN LA COLECCIÓN</p>
                </div>
                <div class="col-12 col-sm-4 col-md-4 ordenar">
                    <i class="fa fa-th select" aria-hidden="true"></i>
                    <i class="fa fa-th-list " aria-hidden="true"></i>
                </div>
            </div>
            <div id="resultados">
                <div class="row">
                    <div class="sel-temas col-6 col-sm-4">
                        <button>
                            <span class="ion-android-checkbox-outline-blank"></span>
                            <span class="ion-android-checkbox-outline"></span>
                            <p class="oswL">Romanticismo y mas</p>
                            <img src="img/agregado-01.jpg">
                        </button>
                    </div>
                </div>
            </div>
            <div class="container paginacion">
                <ul class="azul">
                    <li><a href="#"><i class="ion-ios-arrow-back" aria-hidden="true"></i></a></li>
                    <li><a href="#">1</a></li>
                    <li><a href="#" class="select">2</a></li>
                    <li><a href="#">3</a></li>
                    <li><a href="#">4</a></li>
                    <li><a href="#">5</a></li>
                    <li><a href="#"><i class="ion-ios-arrow-forward" aria-hidden="true"></i></a></li>
                </ul>
            </div>
            <footer class="gris21-bg">
                <div class="container">
                    <div class="logo-cultura">
                        <img src="img/logo-cultura.png" class="img-responsive">
                    </div>
                    <div class="row pie-sube">
                        <a href="#top">
                            <i class="ion-ios-arrow-thin-up" aria-hidden="true"></i>
                        </a>
                    </div>
                    <div class="row datos">
                        <div class="col-7 col-sm-6 col-md-4 col-lg-4 datos1">
                            <ul>
                                <li><a href="#">Quiénes somos y qué hacemos</a></li>
                                <li><a href="#">Nuestros proveedores de datos</a></li>
                                <li><a href="#">Cómo colaborar con nosotros</a></li>
                            </ul>
                        </div>
                        <div class="col-5 col-sm-6 col-md-4 col-lg-4 datos2">
                            <ul>
                                <li><a href="#">Declaración de derechos</a></li>
                                <li><a href="#">Documentación</a></li>
                                <li><a href="#">Red de Preservación</a></li>
                            </ul>
                        </div>
                        <hr class="d-md-none">
                        <div class="col-7 col-sm-6 col-md-4 col-lg-4 datos3">
                            <ul>
                                <li><a href="#">Contacto</a></li>
                                <li><a href="#">1234 5678 ext. 123 y 456</a></li>
                                <li><a href="#">email@cultura.gob.mx</a></li>
                            </ul>
                        </div>
                        <hr class="d-none d-sm-none d-md-block">
                        <div class="col-5 col-sm-6 col-md-12 col-lg-12 datos4">
                            <ul class="row">
                                <li class="col-md-4"><a href="#">Mapa de sitio</a></li>
                                <li class="col-md-4"><a href="#">Política de Privacidad</a></li>
                                <li class="col-md-4"><a href="#">Términos de uso</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </footer>
            <div class="container-fluid pie-derechos">
                <p>Secretaría de Cultura, 2017. Todos los derechos reservados.</p>
            </div>
        </div>
    </div>
</main>