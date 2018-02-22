
var three = undefined;

/* specific 311 filter */
var threeFilterStatus = null; 

/* general key word filter */
var query;
var queryTask;

/* toolbar state */
navbarHidden = true;

$(document).ready(function(){

  // hide navbar info 
  // $("#navbar-info").css('opacity','0');

	/* bind menu handlers */
	$( "button#toggle" ).click(function() {
        $("div#content-left").toggleClass( "slideaway" );
    });
            
    $( "a#info-selector" ).click(function() {
        $("div#other-info").toggleClass( "visible" );
        $("p#infop").toggleClass( "infopclass" );
    });
            
    /* view port height adjustments (accommodate browser)*/
    var viewportHeight = $(window).height();
    $( "div#content-left").css( "height", (viewportHeight-60));
    $( "div#mapDiv").css( "height", (viewportHeight-60));

    /* load the esri map */
    init();

});

function dropbar (argument) {

  if (navbarHidden) {

    // record new navbar state
    navbarHidden = false;

    // expand navbar
    $("#main-navbar").css("height", "350px");

    // show navbar info 
    $("#navbar-info").animate({top: 70, opacity: 1}, 'slow');

    // set img src
    $("#toggleimg").attr('src', 'resources/static/images/menu-cancel.png').fadeIn('slow');
  }

  else {

    // record hidden navbar state
    navbarHidden = true;

    // retract navbar
    $("#main-navbar").css("height", "60px");

    // show navbar info 
    //$("#navbar-info").hide();

    $("#navbar-info").animate({top: 50, opacity: 0}, 'slow');

    // set img src
    $("#toggleimg").attr('src', 'resources/static/images/menu_rectangles.png').fadeIn('slow');
  }


  
  // body...
  console.log("dropping toolbar");

 

}

function filter() {

  /* set filter status (pull modal data) */
  threeFilterStatus = $("input[name=request-status-option]:checked").val();
  console.log("311 filter status: " + threeFilterStatus);

  /* hide modal, keep settings */
  $("#filter-settings-modal").modal('hide');

  /* apply new settings... */
  if (threeFilterStatus == "None") {
    three.setDefinitionExpression("");
  } else {
    three.setDefinitionExpression("Status='" + threeFilterStatus + "'");
  }

  // Test Stuff Here... 
}

function report() {

	/* TODO: show drop down modal that allows people to post geotagged report to 311 */

	/* send email, or fill form... */

	console.log("incident reported!");
}

function init() {

	console.log("application ready, rendering maps...");
      
	require([
    	"esri/map",
      "esri/layers/FeatureLayer",
      "esri/InfoTemplate",
      "esri/dijit/Geocoder",
      "esri/dijit/HomeButton",
      "esri/dijit/LocateButton",
      "esri/tasks/QueryTask",
      "esri/tasks/query",
      "dojo/domReady!"

    ], function(Map, FeatureLayer, InfoTemplate, Geocoder, HomeButton, LocateButton, QueryTask, Query) {

		  /* create the map and add a basemap */
	  	var map = new Map("ui-esri-map", {
	    	center: [-95.3698, 29.7604], // long, lat (houston's center)
	    	zoom: 14,
	    	basemap: "topo"
	  	});

      /* set up geocode search bar */
	  	var geocoder = new Geocoder({
        arcgisGeocoder: {
          placeholder: "Search "
        },
        map: map
      }, "ui-esri-dijit-geocoder");
      geocoder.startup();

      /* set up home button */ 
      var home = new HomeButton({
        theme: "HomeButton",
        map: map,
        extent: null,
        visible: true
      }, "homeButton");
      home.startup();

      /* setup geolocate button */
      geoLocate = new LocateButton({
        map: map
      }, "locateButton");
      geoLocate.startup();

      /* define the info template for 311 data points */
      var threeTemplate = new InfoTemplate();
      threeTemplate.setTitle("311 Service Request");
      
      /* formated input layer fields */
      threeTemplate.setContent(

        "<b>Title  </b> ${Title} <br><br>"+
        "<b>Status  </b> ${Status} <br><br>" +
        "<b>SRNumber  </b> ${SRNumber} <br><br>" +
        "<b>Location  </b> ${Location} <br><br>" +
        "<b>DueDate  </b> ${DueDate} <br><br>" +
        "<b>CreateDate  </b> ${CreateDate} <br><br>" +
        "<b>ClosedData  </b> ${ClosedDate} <br>"
      );

      /* setup 311 feature layer */
	  	three = new FeatureLayer("http://mycity.houstontx.gov/ArcGIS10/rest/services/wm/SR311Display_wm/MapServer/0",{
        mode: esri.layers.FeatureLayer.MODE_ONDEMAND,
        infoTemplate: threeTemplate,
        objectIdField: "SRNumber",
        outFields: ["*"]
      });

      //initialize query task
      // queryTask = new esri.tasks.QueryTask("http://mycity.houstontx.gov/ArcGIS10/rest/services/wm/SR311Display_wm/MapServer/0");

      // query = new Query();
      // query.outFields = ["*"];
      //query.where = "Status = Open";

		  /* three layer data filtering */
      three.setDefinitionExpression("Status='Open'");

      /* add 311 data layer */
		  map.addLayer(three);

  	});
}
