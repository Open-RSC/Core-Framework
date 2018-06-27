<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<ol class="breadcrumb">
	<li><a href="index.php">Home</a></li>
	<li><a href="guide.php">Guides</a></li>
	<li><a href="guide.php?m=quest_db">Quest list</a></li>
	<li class="active"><?php echo $result['title'] ?></li>
</ol>
<div class="sidebar col-sm-3 content-l-side">
<div class="hidden-xs">
        <!-- do magic loop-->
		<div class="list-group-guides">
		<div class="content-header content-header--highlight">
			<h2 class="content-header-title guideColor">F2P Quests</h2>
		</div>
		 <?php
		$quest_free_query = $db->query("SELECT id, title FROM guides WHERE guide_type = 'Free' AND type = '0' ORDER BY id ASC");
		if($db->num_rows($quest_free_query) > 0) {		
			while($pull_guides = $db->fetch_assoc($quest_free_query)) {
				echo  
				'
				<article class="group_guides">
					<a href="guide.php?m=quest_db&id='.$pull_guides['id'].'" class="guide-title_list">
						<strong>' . $pull_guides['title'] . '</strong>
						</a>
					</a>
				</article>
				';
			}
		}
		?>
		</div>
</div>
<?php if(MEMBERS_CONTENT) { ?>
<div class="hidden-xs">
        <!-- do magic loop-->
		<div class="list-group-guides">
		<div class="content-header content-header--highlight">
			<h2 class="content-header-title guideColor">P2P Quests</h2>
		</div>
		 <?php
		$quest_member_query = $db->query("SELECT id, title FROM guides WHERE guide_type = 'Member' AND type = '0' ORDER BY id ASC");
		if($db->num_rows($quest_member_query) > 0) {		
			while($pull_guides = $db->fetch_assoc($quest_member_query)) {
				echo  
				'
				<article class="group_guides">
					<a href="guide.php?m=quest_db&id='.$pull_guides['id'].'" class="guide-title_list">
						<strong>' . $pull_guides['title'] . '</strong>
						</a>
					</a>
				</article>
				';
			}
		}
		?>
		</div>
</div>
<?php } ?>
</div>
<div class="col-sm-9 char-r-side">
	<div class="panel panel-default">
		<div class="content-header content-header--highlight">
			<h2 class="content-header-title"><?php echo $result['title'] ?></h2>
		</div>
		<div class="embended-info">
			<h2 class="description_guide">Description</h2>
			<p class="guide-desc__text">
			<?php echo $result['description'] ?>
			</p>
		</div>
		<div class="guide-button">
			<a class="btn btn-default-2" href="viewforum.php?id=15">Incorrect or missing information (Click here)</a>
		</div>
		<div class="embended-add">
			<p><span><strong><i class="fa fa-lightbulb-o" aria-hidden="true"></i> Quest Start Information</span></strong></p>
		</div>
		<div class="panel-body">
			<div class="col-sm-12">
				<table class="table table-bordered" style="margin: 0 auto;" cellspacing="3">
					<tbody>
					<tr>
						<td style="width:20%; vertical-align:top" class="questdetails-header-alternate"><b>Start point</b>
						</td>
						<td style="width:80%" class="questdetails-info"><img src="img/icons/qloc.png" alt="Quest" width="17" height="17">
						<?php echo $result['start_location'] ?>
						</td>
					</tr>
					<tr>
						<td class="questdetails-header"><b>Official difficulty</b>
						</td>
						<td class="questdetails-info"><?php echo $result['difficulty'] ?>
						</td>
					</tr>
					<tr>
						<td class="questdetails-header"><b>Length</b>
						</td>
						<td class="questdetails-info"><?php echo $result['length'] ?>
						</td>
					</tr>
					<tr>
						<td class="questdetails-header"><b>Requirements</b>
						</td>
						<td class="questdetails-info"><?php echo parse_message($result['reqs']) ?>
						</td>
					</tr>
					<tr>
						<td class="questdetails-header"><b>Items needed<p><small>(hover over picture)</small></p></b>
						</td>
						<td class="questdetails-info"><?php echo $result['items_needed'] ?>
						</td>
					</tr>
					<tr>
						<td style="vertical-align:top" class="questdetails-header-alternate"><b>Quest Points</b>
						</td>
						<td class="questdetails-info"><?php echo $result['quest_points'] ?>
						</td>
					</tr>
						<tr>
						<td class="questdetails-header"><b>Rewards</b>
						</td>
						<td class="questdetails-info"><?php echo parse_message($result['rewards']) ?>
						</td>
					</tr>
					<tr>
						<td style="vertical-align:top" class="questdetails-header-alternate"><b>Written by</b>
						</td>
						<td class="questdetails-by"><strong><?php echo luna_htmlspecialchars($result['poster']) ?></strong>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		</div>
		<div class="embended-add">
			<p><span><strong><i class="fa fa-list" aria-hidden="true"></i> Walkthrough</span></strong></p>
		</div>
		<div class="panel-body">
			<div class="col-sm-12">
				<div class="guide-publish">
					<?php echo parse_message($result['post']) ?>
				</div>
			</div>
		</div>
	</div>
</div>