<!DOCTYPE html>
<html>
<head>
  <title>WattDepot Administration</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- Bootstrap -->
  <link rel="stylesheet" href="/webroot/dist/css/bootstrap.min.css">
  <!-- Optional theme -->
  <link rel="stylesheet" href="/webroot/dist/css/bootstrap-theme.min.css">
  <link rel="stylesheet" href="/webroot/dist/css/themes/blue/style.css">
  <link rel="stylesheet/less" type="text/css" href="/webroot/dist/css/style.less">
  <link rel="stylesheet" type="text/css" href="/webroot/dist/css/parsley.css">

  <script src="/webroot/dist/js/less-1.3.0.min.js"></script>
  <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
  <script src="/webroot/dist/js/jquery.js"></script>
  <script src="/webroot/dist/js/jquery.tablesorter.js"></script>
  <script src="/webroot/dist/js/bootstrap.min.js"></script>
  <script src="/webroot/dist/js/parsley.js"></script>
  <script src="/webroot/dist/js/wattdepot-organization-admin.js"></script>
  <script>
    window.ParsleyValidator
        .addValidator('slug', function (value) {
          return isValidSlug(value);
        }, 32)
        .addMessage('en', 'slug', 'This value should be a slug.');
  </script>

</head>
<body>
<nav class="navbar navbar-default" role="navigation">
  <!-- Brand and toggle get grouped for better mobile display -->
  <div class="navbar-header">
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
      <span class="sr-only">Toggle navigation</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
    <a class="navbar-brand" href="#"><img src="/webroot/dist/wattdepot-logo.png"></a>
  </div>

  <!-- Collect the nav links, forms, and other content for toggling -->
  <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
    <ul class="nav navbar-nav">
      <li class="active"><a href="#">Settings</a></li>
      <li><a href="/wattdepot/${orgId}/summary/">Summary</a></li>
      <li><a href="/wattdepot/${orgId}/visualize/">Chart</a></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="#">${orgId}</a></li>
    </ul>
  </div>
  <!-- /.navbar-collapse -->
</nav>

<div id="modal-dialogs"></div>
<div class="container">
  <!-- Nav tabs -->
  <ul class="nav nav-tabs" id="tabs">
    <li><a id="depositories_tab_link" href="#depositories" data-toggle="tab">Depositories</a></li>
    <li><a id="sensors_tab_link" href="#sensors" data-toggle="tab" name="sensors">Sensors</a></li>
    <li><a id="sensorgroups_tab_link" href="#sensorgroups" data-toggle="tab" name="sensorgroups">Sensor Groups</a></li>
    <li><a id="sensorprocesses_tab_link" href="#sensorprocesses" data-toggle="tab" name="sensorprocesses">Collector
      Process Definitions</a></li>
    <li><a id="measurementpruning_tab_link" href="#measurementpruning" data-toggle="tab" name="measurementpruning">Measurement
      Pruning Definitions</a></li>
  </ul>
  <a name="sensorprocesses"></a>
  <!-- Tab panes -->
  <div class="tab-content">
    <div class="tab-pane active" id="depositories">
      <!--            <div class="well"> -->
      <div class="panel-group" id="help">
        <div class="panel panel-default">
          <div class="panel-heading">
            <a class="panel-title text-right accordion-toggle collapsed" data-toggle="collapse" data-parent="#help"
               href="#depositoryCollapseHelp">Help </a>
          </div>
          <div id="depositoryCollapseHelp" class="panel-collapse collapse">
            <div class="panel-body">
              <p>Depositories store measurements made by Sensors and collected by Collectors.<br>Depositories can store
                Measurements made by different Sensors and different Collectors, but all Measurements must be of one and
                only one Measurement Type.</p>

              <p>You cannot edit Depositories just create them or delete them. Deleting a Depository deletes all the
                measurements stored in the depository.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5"><h3>Depositories</h3></div>
        <div class="col-xs-6"></div>
        <div class="col-xs-1">
          <button data-toggle="modal" data-target="#addDepositoryModal" class="btn btn-primary btn-sm add-button"><span
              class="glyphicon glyphicon-plus"></span></button>
        </div>
      </div>
      <table id="depositoryTable" class="table tablesorter">
        <thead>
        <tr>
          <th>Id</th>
          <th>Name</th>
          <th>Measurement Type</th>
          <th style="width: 7px;"></th>
        </tr>
        </thead>
        <tbody>
        <#list depositories as d>
        <tr>
          <td>${d.id}</td>
          <td>${d.name}</td>
          <td><#if d.getMeasurementType()??>${d.measurementType.name}</#if></td>
          <td>
            <a href="#"><span class="glyphicon glyphicon-remove"
                              onclick="delete_depository_dialog(event, '${d.id}');"></span></a>
          </td>
        </tr>
        </#list>
        </tbody>
      </table>
      <!--            </div>  -->
    </div>
    <div class="tab-pane" id="sensors">
      <!--            <div class="well">  -->
      <div class="panel-group" id="help">
        <div class="panel panel-default">
          <div class="panel-heading">
            <a class="panel-title text-right accordion-toggle collapsed" data-toggle="collapse" data-parent="#help"
               href="#sensorCollapseHelp">Help </a>
          </div>
          <div id="sensorCollapseHelp" class="panel-collapse collapse">
            <div class="panel-body">
              <p>Sensors represent a device that measures (or predicts) physical phenomena.</p>

              <p>Deleting a Sensor deletes all the measurements made by that sensor.</p>

              <p> Be careful when you change the model of a sensor. This can affect the Collector processes and the
                types of measurements the sensor can make.</p>

              <p> To view and edit the defined Sensor Models click the Sensor Models link.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5"><h3>Sensors</h3></div>
        <div class="col-xs-6"></div>
        <div class="col-xs-1">
          <button data-toggle="modal" data-target="#addSensorModal" class="btn btn-primary btn-sm add-button"><span
              class="glyphicon glyphicon-plus"></span></button>
        </div>
      </div>

      <table id="sensorTable" class="table tablesorter">
        <thead>
        <tr>
          <th>Id</th>
          <th>Name</th>
          <th>URI</th>
          <th>Model</th>
          <th>Properties</th>
          <th style="width: 7px;"></th>
          <th style="width: 7px;"></th>
        </tr>
        </thead>
        <tbody>
        <#list sensors as s>
        <tr>
          <td>${s.id}</td>
          <td>${s.name}</td>
          <td>${s.uri}</td>
          <td><#if s.getModelId()??>${s.modelId}</#if></td>
          <td>[<#assign k = s.properties?size>
            <#list s.properties as p>
              <#if p.getKey()??>{"${p.key}":"${p.value}"}</#if>
            <#if k != 1>
            ,</#if><#assign k = k -1></#list>]
          </td>
          <td>
            <a href="#"><span class="glyphicon glyphicon-pencil" onclick="edit_sensor_dialog(event, '${s.id}');"></span></a>
          </td>
          <td>
            <a href="#"><span class="glyphicon glyphicon-remove"
                              onclick="delete_sensor_dialog(event, '${s.id}');"></span></a>
          </td>
        </tr>
        </#list>
        </tbody>
      </table>

      <!--            </div>  -->
      <div class="panel-group" id="accordion">
        <div class="panel panel-default">
          <div class="panel-heading">
            <h4 class="panel-title">
              <a class="accordion-toggle collapsed" data-toggle="collapse" data-parent="#accordion"
                 href="#collapseModel">Sensor Models</a>
            </h4>
          </div>
          <div id="collapseModel" class="panel-collapse collapse">
            <!--                        <div class="well">  -->
            <div class="row">
              <div class="col-xs-5"><h3>Sensor Models</h3></div>
              <div class="col-xs-6"></div>
              <div class="col-xs-1">
                <button data-toggle="modal" data-target="#addModelModal" class="btn btn-primary btn-sm add-button"><span
                    class="glyphicon glyphicon-plus"></span></button>
              </div>
            </div>


            <table id="sensorModelTable" class="table tablesorter">
              <thead>
              <tr>
                <th>Id</th>
                <th>Name</th>
                <th>Protocol</th>
                <th>Type</th>
                <th>Version</th>
                <th style="width: 7px;"></th>
                <th style="width: 7px;"></th>
              </tr>
              </thead>
              <tbody>
              <#list sensormodels as m>
              <tr>
                <td>${m.id}</td>
                <td>${m.name}</td>
                <td>${m.protocol}</td>
                <td>${m.type}</td>
                <td>${m.version}</td>
                <td>
                  <a href="#"><span class="glyphicon glyphicon-pencil"
                                    onclick="edit_model_dialog(event, '${m.id}');"></span></a>
                </td>
                <td>
                  <a href="#"><span class="glyphicon glyphicon-remove"
                                    onclick="delete_model_dialog(event, '${m.id}');"></span></a>
                </td>
              </tr>
              </#list>
              </tbody>
            </table>
            <!--                        </div>  -->
          </div>
        </div>
      </div>
    </div>
    <div class="tab-pane" id="sensorgroups">
      <!--            <div class="well"> -->
      <div class="panel-group" id="help">
        <div class="panel panel-default">
          <div class="panel-heading">
            <a class="panel-title text-right accordion-toggle collapsed" data-toggle="collapse" data-parent="#help"
               href="#sensorGroupCollapseHelp">Help </a>
          </div>
          <div id="sensorGroupCollapseHelp" class="panel-collapse collapse">
            <div class="panel-body">
              <p>Clients often find it convenient to request aggregations of sensor data. For example, a client might
                wish to know the energy consumed by a building, which might involve aggregating the energy measurements
                associated with Sensors located on each floor of the building.</p>

              <p>Sensors may be members of multiple different Sensor Groups.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5"><h3>Sensor Groups</h3></div>
        <div class="col-xs-6"></div>
        <div class="col-xs-1">
          <button data-toggle="modal" data-target="#addSensorGroupModal" class="btn btn-primary btn-sm add-button"><span
              class="glyphicon glyphicon-plus"></span></button>
        </div>
      </div>

      <table id="sensorGroupTable" class="table tablesorter">
        <thead>
        <tr>
          <th>Id</th>
          <th>Name</th>
          <th>Sensors</th>
          <th style="width: 7px;"></th>
          <th style="width: 7px;"></th>
        </tr>
        </thead>
        <tbody>
        <#list sensorgroups as g>
        <tr>
          <td>${g.id}</td>
          <td>${g.name}</td>
          <td><#list g.sensors as s>${s} </#list></td>
          <td>
            <a href="#"><span class="glyphicon glyphicon-pencil"
                              onclick="edit_sensorgroup_dialog(event, '${g.id}');"></span></a>
          </td>
          <td>
            <a href="#"><span class="glyphicon glyphicon-remove"
                              onclick="delete_sensorgroup_dialog(event, '${g.id}');"></span></a>
          </td>
        </tr>
        </#list>
        </tbody>
      </table>

      <!--            </div>  -->
    </div>
    <div class="tab-pane" id="sensorprocesses">
      <!--            <div class="well">  -->
      <div class="panel-group" id="help">
        <div class="panel panel-default">
          <div class="panel-heading">
            <a class="panel-title text-right accordion-toggle collapsed" data-toggle="collapse" data-parent="#help"
               href="#CPDCollapseHelp">Help </a>
          </div>
          <div id="CPDCollapseHelp" class="panel-collapse collapse">
            <div class="panel-body">
              <p>Collectors are processes that contact a Sensor, obtain Measurements from it, then store this data in a
                WattDepot server.</p>

              <p>Collector Process Definitions help define the Collectors. They contain the Sensor, polling interval (in
                seconds), and the Depository the process should be using to store the Measurements.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5"><h3>Collector Process Definitions</h3></div>
        <div class="col-xs-6"></div>
        <div class="col-xs-1">
          <button data-toggle="modal" data-target="#addCPDModal" class="btn btn-primary btn-sm add-button"><span
              class="glyphicon glyphicon-plus"></span></button>
        </div>
      </div>
      <table id="cpdTable" class="table tablesorter">
        <thead>
        <tr>
          <th>Id</th>
          <th>Name</th>
          <th>Sensor</th>
          <th>Polling Interval</th>
          <th>Depository</th>
          <th>Properties</th>
          <th style="width: 7px;"></th>
          <th style="width: 7px;"></th>
        </tr>
        </thead>
        <tbody>
        <#list cpds as p>
        <tr>
          <td>${p.id}</td>
          <td>${p.name}</td>
          <td>${p.sensorId}</td>
          <td>${p.pollingInterval}</td>
          <td>${p.depositoryId}</td>
          <td>[<#assign k = p.properties?size><#list p.properties as prop>{"${prop.key}":"${prop.value}"}<#if k != 1>
            ,</#if><#assign k = k -1></#list>]
          </td>
          <td>
            <a href="#"><span class="glyphicon glyphicon-pencil"
                              onclick="edit_cpd_dialog(event, '${p.id}');"></span></a>
          </td>
          <td>
            <a href="#"><span class="glyphicon glyphicon-remove" onclick="delete_cpd_dialog(event, '${p.id}');"></span></a>
          </td>
        </tr>
        </#list>
        </tbody>
      </table>

      <!--            </div>  -->
    </div>
    <div class="tab-pane" id="measurementpruning">
      <!--            <div class="well">  -->
      <div class="panel-group" id="help">
        <div class="panel panel-default">
          <div class="panel-heading">
            <a class="panel-title text-right accordion-toggle collapsed" data-toggle="collapse" data-parent="#help"
               href="#MPDCollapseHelp">Help </a>
          </div>
          <div id="MPDCollapseHelp" class="panel-collapse collapse">
            <div class="panel-body">
              <p>Measurement pruning is a way of reducing the number of measurements stored in the WattDepot database.
                Most applications will want high resolution measurmement data for a period of time, but not require that
                amount of historical data. The Measurement Pruning Definition allows you to manage how much measurement
                data is kept.</p>

              <p>Measurement Pruning Definitions help define the how much data is kept in the database. They contain the
                Depository, the Sensor, the number of days to keep high resolution data, the collection window a number
                of days to reduce the number of measurements in, and the minimum gap in seconds between measurements. A
                separate process can use this definition to remove measurement in the collection window if they have a
                smaller gap between them than the minimum measurement gap.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-xs-5"><h3>Measurement Pruning Definitions</h3></div>
        <div class="col-xs-6"></div>
        <div class="col-xs-1">
          <button data-toggle="modal" data-target="#addMPDModal" class="btn btn-primary btn-sm add-button"><span
              class="glyphicon glyphicon-plus"></span></button>
        </div>
      </div>
      <table id="MPDTable" class="table tablesorter">
        <thead>
        <tr>
          <th>Id</th>
          <th>Name</th>
          <th>Depository</th>
          <th>Sensor</th>
          <th>Ignore Window (Days)</th>
          <th>Collection Window (Days)</th>
          <th>Minimum Gap (sec)</th>
          <th>Last Run</th>
          <th>Duration (sec)</th>
          <th>Measurements Pruned</th>
          <th>Expected Next Run</th>
          <th style="width: 7px;"></th>
          <th style="width: 7px;"></th>
        </tr>
        </thead>
        <tbody>
        <#list mpds as g>
        <tr>
          <td>${g.id}</td>
          <td>${g.name}</td>
          <td>${g.depositoryId}</td>
          <td>${g.sensorId!""}</td>
          <td>${g.ignoreWindowDays}</td>
          <td>${g.collectWindowDays}</td>
          <td>${g.minGapSeconds}</td>
          <td>${(g.lastStarted?datetime)!"Never"}</td>
          <td>${(g.getDuration() / 1000)!0}</td>
          <td>${g.numMeasurementsCollected!0}</td>
          <td>${(g.nextRun?datetime)!"Now"}</td>
          <td>
            <a href="#"><span class="glyphicon glyphicon-pencil"
                              onclick="edit_MPD_dialog(event, '${g.id}');"></span></a>
          </td>
          <td>
            <a href="#"><span class="glyphicon glyphicon-remove" onclick="delete_MPD_dialog(event, '${g.id}');"></span></a>
          </td>
        </tr>
        </#list>
        </tbody>
      </table>

      <!--            </div>  -->
    </div>
  </div>

  <!-- ********************** Depository Modal Dialog Boxes **************************** -->
  <!-- Add Depository -->
  <div class="modal fade" id="addDepositoryModal" tabindex="-1"
       role="dialog" aria-labelledby="addDepositoryModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
                  aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add Depository</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="add-depository">
              <div class="form-group">
                <label class="col-md-3 control-label">Depository
                  Id</label>

                <div class="col-md-9">
                  <input type="text" name="depository_id" class="form-control" required data-parsley-slug/>

                  <p class="help-block">Unique Depository id and be a slug. Slugs consist of lowercase letter, numbers
                    and '-', no other characters are allowed.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label">Depository
                  Name</label>

                <div class="col-md-9">
                  <input type="text" name="depository_name" class="form-control" required/>

                  <p class="help-block">Unique name.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label">Depository
                  Measurement Type</label>

                <div class="col-md-9">
                  <select class="form-control" name="depository_type" required>
                    <option value="">Choose...</option>
                  <#list measurementtypes as mt>
                    <option value="${mt.id}">${mt.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">The type of measurement the depository stores.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="putNewDepository();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Delete Depository -->
  <div class="modal fade" id="deleteDepositoryModal" tabindex="-1"
       role="dialog" aria-labelledby="deleteDepositoryModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
                  aria-hidden="true">&times;</button>
          <h4 class="modal-title">Delete Depository</h4>
        </div>
        <div class="modal-body">
          <p>
            <b>Delete Depository </b>
          </p>

          <div id="del_depository_id"></div>
          <p><em>WARNING</em> All measurements in this depository will be deleted.</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default"
                  data-dismiss="modal">Close
          </button>
          <button id="delete_button" type="button"
                  class="btn btn-primary"
                  onclick="deleteDepository();">Delete
            Depository
          </button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- / .modal -->



  <!-- ********************** Sensor Modal Dialog Boxes **************************** -->
  <!-- Add Sensor -->
  <div class="modal fade" id="addSensorModal" tabindex="-1" role="dialog" aria-labelledby="addSensorModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add Sensor</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="add-sensor">
              <div class="form-group">
                <label class="col-md-3 control-label">Sensor Id</label>

                <div class="col-md-9">
                  <input type="text" name="sensor_id" class="form-control" required data-parsley-slug/>

                  <p class="help-block">Sensor id must be unique and be a slug. Slugs consist of lowercase letter,
                    numbers and '-', no other characters are allowed.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="sensor_id">Sensor Name</label>

                <div class="col-md-9">
                  <input type="text" name="sensor_name" class="form-control" required/>

                  <p class="help-block">Sensor names must be unique.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="sensor_uri">Sensor URI</label>

                <div class="col-md-9">
                  <input type="text" name="sensor_uri" class="form-control" required/>

                  <p class="help-block">The URI to contact the sensor.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="sensor_model">Model</label>

                <div class="col-md-9">
                  <select class="form-control" name="sensor_model" required>
                    <option value="">Choose...</option>
                  <#list sensormodels as sm>
                    <option value="${sm.id}">${sm.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the model for the sensor.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Properties</label>

                <div class="col-sm-9">
                  <div id="sensor_properties">
                    <table id="sensor_prop_table">
                      <thead>
                      <th>Property</th>
                      <th>Value</th>
                      </thead>
                      <tbody id="add_sensor_props">
                      <tr>
                        <td>
                          <input type="text" name="sensor_prop1" class="form-control">
                        </td>
                        <td>
                          <input type="text" name="sensor_val1" class="form-control" onchange="addSensorProp()">
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </div>
                  <p class="help-block">List of the Sensor's Properties.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="putNewSensor();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Edit Sensor -->
  <div class="modal fade" id="editSensorModal" tabindex="-1" role="dialog" aria-labelledby="editSensorModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Edit Sensor</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="edit-sensor">
              <div class="form-group">
                <label class="col-md-3 control-label">Sensor Id</label>

                <div class="col-md-9">
                  <input type="text" name="edit_sensor_id" class="form-control" disabled>

                  <p class="help-block">You cannot change a Sensor id once it is created.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="sensor_id">Sensor Name</label>

                <div class="col-md-9">
                  <input type="text" name="edit_sensor_name" class="form-control" required/>

                  <p class="help-block">Sensor names must be unique.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="sensor_uri">Sensor URI</label>

                <div class="col-md-9">
                  <input type="text" name="edit_sensor_uri" class="form-control" required/>

                  <p class="help-block">The URI to contact the sensor.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="sensor_model">Model</label>

                <div class="col-md-9">
                  <select class="form-control" name="edit_sensor_model" required>
                  <#list sensormodels as sm>
                    <option value="${sm.id}">${sm.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the model for the sensor.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Properties</label>

                <div class="col-sm-9">
                  <div id="edit_sensor_properties">
                    <table id="edit_sensor_prop_table">
                      <thead>
                      <th>Property</th>
                      <th>Value</th>
                      </thead>
                      <tbody id="edit_sensor_props">
                      </tbody>
                    </table>
                  </div>
                  <p class="help-block">List of the Sensor's Properties.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="updateSensor();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Delete Sensor -->
  <div class="modal fade" id="deleteSensorModal" tabindex="-1"
       role="dialog" aria-labelledby="deleteSensorModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
                  aria-hidden="true">&times;</button>
          <h4 class="modal-title">Delete Sensor</h4>
        </div>
        <div class="modal-body">
          <p>
            <b>Delete Sensor </b>
          </p>

          <div id="del_sensor_id"></div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default"
                  data-dismiss="modal">Close
          </button>
          <button id="delete_button" type="button"
                  class="btn btn-primary" onclick="deleteSensor();">Delete
            Sensor
          </button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- / .modal -->

  <!-- ********************** SensorModel Modal Dialog Boxes **************************** -->
  <!-- Add SensorModel -->
  <div class="modal fade" id="addModelModal" tabindex="-1" role="dialog"
       aria-labelledby="addModelModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
                  aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add Sensor Model</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="add-sensor-model">
              <div class="form-group">
                <label class="col-md-3 control-label">Sensor Model Id</label>

                <div class="col-md-9">
                  <input type="text" name="model_id" class="form-control" required data-parsley-slug/>

                  <p class="help-block">Sensor Model id must be unique and be a slug. Slugs consist of lowercase letter,
                    numbers and '-', no other characters are allowed.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label">Sensor Model Name</label>

                <div class="col-md-9">
                  <input type="text" name="model_name" class="form-control" required/>

                  <p class="help-block">Name of the Sensor Model.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label">Protocol</label>

                <div class="col-md-9">
                  <input type="text" name="model_protocol" class="form-control" required/>

                  <p class="help-block">The protocol the sensor follows.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label">Type</label>

                <div class="col-md-9">
                  <input type="text" name="model_type" class="form-control" required/>

                  <p class="help-block">The type of the model.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label">Version</label>

                <div class="col-md-9">
                  <input type="text" name="model_version" class="form-control" required/>

                  <p class="help-block">The version of the model.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="putNewModel();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Edit SensorModel -->
  <div class="modal fade" id="editModelModal" tabindex="-1" role="dialog"
       aria-labelledby="editModelModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
                  aria-hidden="true">&times;</button>
          <h4 class="modal-title">Edit Sensor Model</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="edit-sensor-model">
              <div class="form-group">
                <label class="col-md-3 control-label">Sensor Model Id</label>

                <div class="col-md-9">
                  <input type="text" name="edit_model_id" class="form-control" disabled>

                  <p class="help-block">Sensor Model id cannot be changed once it is created.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label">Sensor Model Name</label>

                <div class="col-md-9">
                  <input type="text" name="edit_model_name" class="form-control" required/>

                  <p class="help-block">Name of the Sensor Model.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label">Protocol</label>

                <div class="col-md-9">
                  <input type="text" name="edit_model_protocol" class="form-control" required/>

                  <p class="help-block">The protocol the sensor follows.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label">Type</label>

                <div class="col-md-9">
                  <input type="text" name="edit_model_type" class="form-control" required/>

                  <p class="help-block">The type of the model.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label">Version</label>

                <div class="col-md-9">
                  <input type="text" name="edit_model_version" class="form-control" required/>

                  <p class="help-block">The version of the model.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="updateModel();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Delete SensorModel -->
  <div class="modal fade" id="deleteModelModal" tabindex="-1"
       role="dialog" aria-labelledby="deleteModelModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
                  aria-hidden="true">&times;</button>
          <h4 class="modal-title">Delete Sensor Model</h4>
        </div>
        <div class="modal-body">
          <p>
            <b>Delete Sensor Model </b>
          </p>

          <div id="del_model_id"></div>
          <p><em>Warning</em> deleting a Sensor Model will delete all the Sensors with this model. Deleting Sensors will
            delete the measurements made by the sensors.</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default"
                  data-dismiss="modal">Close
          </button>
          <button id="delete_button" type="button"
                  class="btn btn-primary" onclick="deleteModel();">Delete
            Sensor Model
          </button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- / .modal -->


  <!-- ********************** SensorGroup Modal Dialog Boxes **************************** -->
  <!-- Add Sensor Group -->
  <div class="modal fade" id="addSensorGroupModal" tabindex="-1" role="dialog"
       aria-labelledby="addSensorGroupModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add Sensor Group</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="add-sensor-group">
              <div class="form-group">
                <label class="col-md-3 control-label">Sensor Group Id</label>

                <div class="col-md-9">
                  <input type="text" name="sensorgroup_id" class="form-control" required data-parsley-slug/>

                  <p class="help-block">Sensor Group id must be unique and be a slug. Slugs consist of lowercase letter,
                    numbers and '-', no other characters are allowed.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="sensorgroup_name">Group Name</label>

                <div class="col-md-9">
                  <input type="text" name="sensorgroup_name" class="form-control" required/>

                  <p class="help-block">The unique name of the group.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="groupsensors">Sensors</label>

                <div class="col-md-9">
                  <select class="form-control" name="groupsensors" multiple="multiple" required/>
                  <#list sensors as s>
                    <option value="${s.id}">${s.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the sensors in this group.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="putNewSensorGroup();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Edit Sensor Group -->
  <div class="modal fade" id="editSensorGroupModal" tabindex="-1" role="dialog"
       aria-labelledby="editSensorGroupModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Edit Sensor Group</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="edit-sensor-group">
              <div class="form-group">
                <label class="col-md-3 control-label">Sensor Group Id</label>

                <div class="col-md-9">
                  <input type="text" name="edit_sensorgroup_id" class="form-control" disabled>

                  <p class="help-block">Sensor Group id cannot be changed once created.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="edit_sensorgroup_name">Group Name</label>

                <div class="col-md-9">
                  <input type="text" name="edit_sensorgroup_name" class="form-control" required/>

                  <p class="help-block"></p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-3 control-label" for="edit_groupsensors">Sensors</label>

                <div class="col-md-9">
                  <select class="form-control" name="edit_groupsensors" multiple="multiple" required/>
                  <#list sensors as s>
                    <option value="${s.id}">${s.name}</option>
                  </#list>
                  </select>

                  <p class="help-block"></p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="updateSensorGroup();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Delete SensorGroup -->
  <div class="modal fade" id="deleteSensorGroupModal" tabindex="-1"
       role="dialog" aria-labelledby="deleteSensorGroupModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
                  aria-hidden="true">&times;</button>
          <h4 class="modal-title">Delete Sensor Group</h4>
        </div>
        <div class="modal-body">
          <p>
            <b>Delete Sensor Group </b>
          </p>

          <div id="del_sensorgroup_id"></div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default"
                  data-dismiss="modal">Close
          </button>
          <button id="delete_button" type="button"
                  class="btn btn-primary"
                  onclick="deleteSensorGroup();">Delete
            Sensor Group
          </button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- / .modal -->

  <!-- ********************** CollectorProcessDefinition Modal Dialog Boxes **************************** -->
  <!-- Add Collector Process Defintion -->
  <div class="modal fade" id="addCPDModal" tabindex="-1" role="dialog" aria-labelledby="addCPDModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add Collector Process Definition</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="add-cpd">
              <input type="hidden" name="meta_id" value="">

              <div class="form-group">
                <label class="col-md-3 control-label">Collector Process Definition Id</label>

                <div class="col-md-9">
                  <input type="text" name="cpd_id" class="form-control" required data-parsley-slug/>

                  <p class="help-block">Collector Process Definition id must be unique and be a slug. Slugs consist of
                    lowercase letter, numbers and '-', no other characters are allowed.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Collector Name</label>

                <div class="col-sm-9">
                  <input class="form-control" type="text" name="cpd_name" class="form-control" required/>

                  <p class="help-block">Unique name for the definition.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Sensor</label>

                <div class="col-sm-9">
                  <select class="form-control" name="cpd_sensor" required>
                    <option value="">Choose...</option>
                  <#list sensors as s>
                    <option value="${s.id}">${s.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the sensor making measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Polling Interval</label>

                <div class="col-sm-9">
                  <input class="form-control" type="number" name="cpd_polling" class="form-control" required data-parsley-type="integer">

                  <p class="help-block">Number of seconds between measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Depository</label>

                <div class="col-sm-9">
                  <select class="form-control" name="cpd_depository" required>
                    <option value="">Choose...</option>
                  <#list depositories as d>
                    <option value="${d.id}">${d.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the depository to store the measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Properties</label>

                <div class="col-sm-9">
                  <div id="cpd_properties">
                    <table id="cpd_prop_table">
                      <thead>
                      <th>Property</th>
                      <th>Value</th>
                      </thead>
                      <tbody id="add_cpd_props">
                      <tr>
                        <td>
                          <input type="text" name="cpd_prop1" class="form-control">
                        </td>
                        <td>
                          <input type="text" name="cpd_val1" class="form-control" onchange="addCPDProp()">
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </div>
                  <p class="help-block">List of the Collector's Properties.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <!-- /.modal-body -->
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="putNewCPD();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Edit Collector Process Defintion -->
  <div class="modal fade" id="editCPDModal" tabindex="-1" role="dialog" aria-labelledby="editCPDModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Edit Collector Process Definition</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="edit-cpd">
              <input type="hidden" name="meta_id" value="">

              <div class="form-group">
                <label class="col-md-3 control-label">Collector Process Definition Id</label>

                <div class="col-md-9">
                  <input type="text" name="edit_cpd_id" class="form-control" disabled>

                  <p class="help-block">Collector Process Definition id cannot be changed once created.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Collector Name</label>

                <div class="col-sm-9">
                  <input class="form-control" type="text" name="edit_cpd_name" class="form-control" required/>

                  <p class="help-block">Unique name for the definition.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Sensor</label>

                <div class="col-sm-9">
                  <select class="form-control" name="edit_cpd_sensor" required/>
                  <#list sensors as s>
                    <option value="${s.id}">${s.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the sensor making measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Polling Interval</label>

                <div class="col-sm-9">
                  <input class="form-control" type="number" name="edit_cpd_polling" class="form-control" required data-parsley-type="integer">

                  <p class="help-block">Number of seconds between measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Depository</label>

                <div class="col-sm-9">
                  <select class="form-control" name="edit_cpd_depository" required>
                  <#list depositories as d>
                    <option value="${d.id}">${d.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the depository to store the measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Properties</label>

                <div class="col-sm-9">
                  <div id="edit_cpd_properties">
                    <table id="edit_cpd_prop_table">
                      <thead>
                      <th>Property</th>
                      <th>Value</th>
                      </thead>
                      <tbody id="edit_cpd_props">
                      </tbody>
                    </table>
                  </div>
                  <p class="help-block">List of the Collector's Properties.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <!-- /.modal-body -->
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="updateCPD();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Delete Collector Process Definition -->
  <div class="modal fade" id="deleteSensorProcessModal" tabindex="-1"
       role="dialog" aria-labelledby="deleteSensorProcessModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
                  aria-hidden="true">&times;</button>
          <h4 class="modal-title">Delete Collector Metadata</h4>
        </div>
        <div class="modal-body">
          <p>
            <b>Delete Collector Metadata </b>
          </p>

          <div id="del_sensorprocess_id"></div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default"
                  data-dismiss="modal">Close
          </button>
          <button id="delete_button" type="button"
                  class="btn btn-primary"
                  onclick="deleteCPD();">Delete
            Collector Metadata
          </button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- / .modal -->

  <!-- ********************** MeasurementPruningDefinition Modal Dialog Boxes **************************** -->
  <!-- Add Measurement Pruning Defintion -->
  <div class="modal fade" id="addMPDModal" tabindex="-1" role="dialog" aria-labelledby="addMPDModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Add Measurement Pruning Definition</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="add-mpd">
              <input type="hidden" name="meta_id" value="">

              <div class="form-group">
                <label class="col-md-3 control-label">Id</label>

                <div class="col-md-9">
                  <input type="text" name="MPD_id" class="form-control" required data-parsley-slug/>

                  <p class="help-block">The id must be unique and be a slug. Slugs consist of lowercase letter, numbers
                    and '-', no other characters are allowed.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Name</label>

                <div class="col-sm-9">
                  <input class="form-control" type="text" name="MPD_name" class="form-control" required/>

                  <p class="help-block">Unique name for the measurement pruning definition.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Depository</label>

                <div class="col-sm-9">
                  <select class="form-control" name="MPD_depository" required>
                    <option value="">Choose...</option>
                  <#list depositories as d>
                    <option value="${d.id}">${d.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the depository storing the measurements to be pruned.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Sensor</label>

                <div class="col-sm-9">
                  <select class="form-control" name="MPD_sensor" required>
                    <option value="">Choose...</option>
                  <#list sensors as s>
                    <option value="${s.id}">${s.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the sensor that made the measurements to be pruned.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Ignore Window</label>

                <div class="col-sm-9">
                  <input class="form-control" type="number" name="MPD_ignore" class="form-control" required data-parsley-type="integer"/>

                  <p class="help-block">Number of days of high resolution measurement data. This data is not considered
                    for pruning.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Collection Window</label>

                <div class="col-sm-9">
                  <input class="form-control" type="number" name="MPD_collect" class="form-control" required data-parsley-type="integer"/>

                  <p class="help-block">Number of days for the collection window. This data is considered for
                    pruning.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Minimum Gap</label>

                <div class="col-sm-9">
                  <input class="form-control" type="number" name="MPD_gap" class="form-control" required data-parsley-type="integer"/>

                  <p class="help-block">Minimum number of seconds between measurements. During the pruning window.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <!-- /.modal-body -->
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="putNewMPD();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Edit Measurement Pruning Defintion -->
  <div class="modal fade" id="editMPDModal" tabindex="-1" role="dialog" aria-labelledby="editMPDModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Edit Measurement Pruning Definition</h4>
        </div>
        <div class="modal-body">
          <div class="container">
            <form id="edit-mpd">
              <input type="hidden" name="MPD_id" value="">

              <div class="form-group">
                <label class="col-md-3 control-label">Id</label>

                <div class="col-md-9">
                  <input type="text" name="edit_MPD_id" class="form-control" disabled>

                  <p class="help-block">The id cannot be changed once created.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Name</label>

                <div class="col-sm-9">
                  <input class="form-control" type="text" name="edit_MPD_name" class="form-control" required/>

                  <p class="help-block">Unique name for the definition.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Depository</label>

                <div class="col-sm-9">
                  <select class="form-control" name="edit_MPD_depository" required>
                  <#list depositories as d>
                    <option value="${d.id}">${d.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the depository storing the measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Sensor</label>

                <div class="col-sm-9">
                  <select class="form-control" name="edit_MPD_sensor" required>
                  <#list sensors as s>
                    <option value="${s.id}">${s.name}</option>
                  </#list>
                  </select>

                  <p class="help-block">Select the sensor that made the measurements.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Ignore Window</label>

                <div class="col-sm-9">
                  <input class="form-control" type="number" name="edit_MPD_ignore" class="form-control" required data-parsley-type="integer"/>

                  <p class="help-block">Number of days to ignore.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Collection Window</label>

                <div class="col-sm-9">
                  <input class="form-control" type="number" name="edit_MPD_collect" class="form-control" required data-parsley-type="integer"/>

                  <p class="help-block">Number of days in the collection window.</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Minimum Gap</label>

                <div class="col-sm-9">
                  <input class="form-control" type="number" name="edit_MPD_gap" class="form-control" reqiured data-parsley-type="integer"/>

                  <p class="help-block">Minimum gap between measurements in seconds.</p>
                </div>
              </div>
              <div class="clearfix"></div>
            </form>
          </div>
        </div>
        <!-- /.modal-body -->
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" onclick="updateMPD();">Save changes</button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- /.modal -->

  <!-- Delete Measurement Pruning Definition -->
  <div class="modal fade" id="deleteMPDModal" tabindex="-1"
       role="dialog" aria-labelledby="deleteMPDModalLabel"
       aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal"
                  aria-hidden="true">&times;</button>
          <h4 class="modal-title">Delete Measurement Pruning Definition</h4>
        </div>
        <div class="modal-body">
          <p>
            <b>Delete Measurement Pruning Definition </b>
          </p>

          <div id="del_MPD_id"></div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default"
                  data-dismiss="modal">Close
          </button>
          <button id="delete_button" type="button"
                  class="btn btn-primary"
                  onclick="deleteMPD();">Delete
            Measurement Pruning Definition
          </button>
        </div>
      </div>
      <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
  </div>
  <!-- / .modal -->


</div>
<script>
  $('#add-depository').parsley();
  $('#add-sensor').parsley();
  $('#edit-sensor').parsley();
  $('#add-sensor-model').parsley();
  $('#edit-sensor-model').parsley();
  $('#edit-sensor-group').parsley();
  $('#add-cpd').parsley();
  $('#edit-cpd').parsley();
  $('#add-mpd').parsley();
  $('#edit-mpd').parsley();
</script>

<script>
  $(document).ready(function () {
    var selected_tab = getCookie("selected-tab");
    if (selected_tab != null) {
      $('#' + selected_tab + '_tab_link').tab('show');
    }
    else {
      $('#depositories_tab_link').tab('show');
    }
    $("#depositoryTable").tablesorter();
    $("#sensorTable").tablesorter();
    $("#sensorModelTable").tablesorter();
    $("#sensorGroupTable").tablesorter();
    $("#cpdTable").tablesorter();
    $("#MPDTable").tablesorter();
  });

  $('#tabs a').click(function (e) {
    e.preventDefault();
    $(this).tab('show');
  });

  // store the currently selected tab in the hash value
  $("ul.nav-tabs > li > a").on("shown.bs.tab", function (e) {
    var id = $(e.target).attr("href").substr(1);
    setCookie('selected-tab', id);
  });


  var ORGID = "${orgId}";
  var DEPOSITORIES = {};
  <#list depositories as d>
  DEPOSITORIES["${d.id}"] = {
    "id": "${d.id}",
    "name": "${d.name}",
    "measurementType": "${d.measurementType.id}",
    "organizationId": "${d.organizationId}"
  };
  </#list>
  var MODELS = {};
  <#list sensormodels as m>
  MODELS["${m.id}"] = {
    "id": "${m.id}",
    "name": "${m.name}",
    "protocol": "${m.protocol}",
    "type": "${m.type}",
    "version": "${m.version}"
  };
  </#list>
  var SENSORS = {};
  <#list sensors as s>
  SENSORS["${s.id}"] = {
    "id": "${s.id}",
    "name": "${s.name}",
    "uri": "${s.uri}",
    "modelId": "<#if s.getModelId()??>${s.modelId}</#if>",
    "organizationId": "${s.organizationId}",
    "properties": [<#assign k = s.properties?size><#list s.properties as p>{
      "key": "${p.key}",
      "value": "${p.value}"
    }<#if k != 1>,</#if><#assign k = k -1></#list>]
  };
  </#list>
  var SENSORGROUPS = {};
  <#list sensorgroups as sg>
  SENSORGROUPS["${sg.id}"] = {
    "id": "${sg.id}", "name": "${sg.name}", "sensors": [
      <#assign sgLen = sg.sensors?size>
      <#list sg.sensors as s>
        {"id": "${s}"}<#if sgLen != 1>,</#if><#assign sgLen = sgLen - 1>
      </#list>
    ], "organizationId": "${sg.organizationId}"
  };
  </#list>
  var CPDS = {};
  <#list cpds as sp>
  CPDS["${sp.id}"] = {
    "id": "${sp.id}",
    "name": "${sp.name}",
    "sensorId": "${sp.sensorId}",
    "pollingInterval": ${sp.pollingInterval?string.computer},
    "depositoryId": "${sp.depositoryId}",
    "organizationId": "${sp.organizationId}",
    "properties": [<#assign k = sp.properties?size><#list sp.properties as p>{
      "key": "${p.key}",
      "value": "${p.value}"
    }<#if k != 1>,</#if><#assign k = k -1></#list>]
  };
  </#list>
  var MPDS = {};
  <#list mpds as g>
  MPDS["${g.id}"] = {
    "id": "${g.id}",
    "name": "${g.name}",
    "depositoryId": "${g.depositoryId}",
    "sensorId": "${g.sensorId}",
    "organizationId": "${g.organizationId}",
    "ignoreWindowDays": ${g.ignoreWindowDays},
    "collectWindowDays": ${g.collectWindowDays},
    "minGapSeconds": ${g.minGapSeconds?string.computer},
    "lastStarted": "${(g.lastStarted?datetime)!"Never"}",
    "lastCompleted": "${(g.lastCompleted?datetime)!"Never"}",
    "numMeasurementsCollected": ${g.numMeasurementsCollected?string.computer!0}};
  </#list>
  var MEASUREMENTTYPES = {};
  <#list measurementtypes as mt>
  MEASUREMENTTYPES["${mt.id}"] = {"id": "${mt.id}", "name": "${mt.name}", "units": "${mt.units}"};
  </#list>
</script>
</body>
</html>