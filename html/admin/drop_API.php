<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'drop_API');

require LUNA_ROOT.'include/common.php';

if ($luna_user['group_id'] != 1) {
	exit;
}

$action = isset($_GET['action']) ? $_GET['action'] : null;
$id = isset($_GET['id']) ? $_GET['id'] : null;
$id = ctype_digit($id) ? $id : null;

if (isset($action) && isset($id)) {

    $pdo = new PDO('mysql:dbname=wolf_kingdom;host=127.0.0.1', 'root', 'malware');

    if ($action === 'fetch') {
        $fetch = $pdo->prepare('
            SELECT id, amount, weight FROM `' . GAME_BASE . 'npcdrops`
            WHERE npcdef_id = :id
        ');

        $fetch->execute(array(':id' => $id));
        $drops = $fetch->fetchAll(PDO::FETCH_ASSOC);

        if (count($drops) < 1) {
            http_response_code(404);
            die();
        }

        echo json_encode($drops, JSON_NUMERIC_CHECK);
    } else if ($action === 'save') {
        $drops = isset($_POST['items']) ? $_POST['items'] : null;

        if ($drops) {
            $drops = json_decode(urldecode($drops), true);
        }

        $remove = $pdo->prepare('
            DELETE FROM `' . GAME_BASE . 'npcdrops` WHERE npcdef_id = :id
        ');

        $remove->execute(array(':id' => $id));

        $add = $pdo->prepare('
            INSERT INTO `' . GAME_BASE . 'npcdrops` (npcdef_id, id, amount, weight)
            VALUES (:id, :item_id, :amount, :weight)
        ');

        foreach ($drops as $drop) {
            $add->bindValue(':id', $id);
            $add->bindValue(':item_id', $drop['id']);
            $add->bindValue(':amount', $drop['amount']);
            $add->bindValue(':weight', $drop['weight']);
            $add->execute();
        }
    }

    die();
}

require 'header.php';	
?>

<style>
	.npc-id-header {
		width: 5%;
	}

	.npc-name-header {
		width: 80%;
	}

	.npc-level-header {
		width: 20%;
	}

	.results-wrap {
		max-height: 300px;
		overflow-y: auto;
	}

	.results-item {
		cursor: pointer;
	}

	.drops-item-header, .drops-chance-header {
		width: 20%;
	}

	.drops-amount-header, .drops-weight-header {
		width: 25%;
	}

	.drops-remove-header {
		width: 10%;
	}

	.total-drop-header {
		width: 90%;
	}
</style>
<div class="content_wrapper col-sm-10">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">NPCDrops API</h2>
		</div>
	</div>
	<div class="small-12 columns">
		<div id="npcdrop_template">
			 <div class="drops_search">
                    <input id="drops-npc-search" class="form-control" type="search" placeholder="Enter an NPC ID or name to search..." autofocus />

                    <br />

                    <div class="results-wrap">
                        <table class="base-tbl table-hover">
                            <thead>
                                <tr>
                                    <th class="npc-id-header">ID</th>
                                    <th class="npc-name-header">Name</th>
                                    <th class="npc-level-header">Level</th>
                                </tr>
                            </thead>
                            <tbody id="drops-npc-results"></tbody>
                        </table>
                    </div>
                </div>
			<div id="drops-request" class="panel panel-info">
                <div class="panel-heading">
                    <h3 class="panel-title">Please Select an NPC</h3>
                </div>
                <div class="panel-body">
                    Search and select for an NPC above to begin the editing process.
                </div>
            </div>
			
			<div  id="drops-main">
                <div class="col-md-4">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title">Item Selection</h3>
                        </div>
                        <div class="panel-body">
                            <input id="drops-item-search" class="form-control" type="search" placeholder="Enter an item ID or name to search..." />

                            <br />

                            <div class="results-wrap">
                                <table class="base-tbl table-hover">
                                    <tbody id="drops-item-results"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-md-8">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title">Editing "<span id="drops-npc-name"></span>" Drops</h3>
                        </div>
                        <div class="panel-body">
                            <table class="base-tbl">
                                <tr>
                                    <th class="total-drop-header">Total Drops</th>
                                    <td id="drops-total-drops">0</td>
                                </tr>
                                <tr>
                                    <th>Total Drop Weight</th>
                                    <td id="drops-total-weight">0</td>
                                </tr>
                            </table>

                            <table class="base-tbl">
                                <thead>
                                    <tr>
                                        <th class="drops-item-header">Item</th>
                                        <th class="drops-amount-header">Amount</th>
                                        <th class="drops-weight-header">Weight</th>
                                        <th class="drops-chance-header">Chance</th>
                                        <th class="drops-remove-header">&nbsp;</th>
                                    </tr>
                                </thead>
                                <tbody id="drops-drop-results"></tbody>
                            </table>
							<?php 
								if($luna_user['group_id'] == 1) {
									echo '<button id="drops-save" class="btn btn-primary" title="Save your edits to the database.">
														<span class="fa fa-save"></span>
										&nbsp;Save Drops
											</button>';
								}
							?>
                       

                            <button id="drops-reload" class="btn btn-default" title="Reloading will reset any changes you made.">
                                <span class="fa fa-refresh"></span>
                                &nbsp;Reload Drops
                            </button>

                            <button id="drops-clear" class="btn btn-danger pull-right" title="Remove all of the current NPC's drops.">
                                <span class="fa fa-trash"></span>
                                &nbsp;Clear All
                            </button>

                            <br /><br />

                            <button id="drops-copy" class="btn btn-default" title="Copy the current NPC's drops to the clipboard.">
                                <span class="fa fa-file"></span>
                                &nbsp;Copy
                            </button>

                            <button id="drops-paste" class="btn btn-default" title="Paste the last copied drops to the current NPC.">
                                <span class="fa fa-pencil"></span>
                                &nbsp;Paste
                            </button>
                        </div>
                    </div>
                </div>
            </div>
			 <div>
                <p class="text-right">Copyright &copy; 2015-2016 RSCLegacy</p>
            </div>
		</div>
	</div>
</div>
<script src="js/drops.js"></script>
<?php
require 'footer.php';