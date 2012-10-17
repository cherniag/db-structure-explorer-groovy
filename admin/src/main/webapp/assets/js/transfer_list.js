$(document).ready(function() {
	filterButton = $('#filterButton');
	saveButton = $('#saveButton');
	removeButton = $('#removeButton');
	addButton = $('#addButton');
	filterItem = $('#filterItem');
	sourceList = $('#sourceList');
	resultList = $('#resultList');
	
	saveButton.click(function() {
		saveResultList();
	});
	
	filterButton.click(function() {
		filterSourceList();
	});
	
	addButton.click(function() {
		$('#sourceList option:selected').each(function(i) {
			var result = $('#resultList option[value="'+$(this).val()+'"]');
			if(result.size() == 0)
				($(this).clone()).appendTo('#resultList');
		});
	});
	
	removeButton.click(function() {
		return !$('#resultList option:selected').remove();
	});
});

function saveResultList() {
	$("#resultList option").attr("selected","selected");
}

function filterSourceList() {
	if(!filterUrl)
		return;

	var filterStr = filterItem.val();
	
	$.ajax({
		url : filterUrl,
		dataType : "json",
		data : {"filter": filterStr},
		success : function(data, textStatus) {
			sourceList.empty();
			$.each(data, function(i, val) {
				sourceList.append(new Option(val.title, val.id));
			});
		}
	});
}
