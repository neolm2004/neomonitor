(function($) {
	$
			.widget(
					"custom.combobox",
					{
						_create : function() {
							this.wrapper = $("<span>").addClass(
									"custom-combobox")
									.insertAfter(this.element);
							this.element.hide();
							this._createAutocomplete();
							this._createShowAllButton();
						},
						_createAutocomplete : function() {
							var selected = this.element.children(":selected"), value = selected
									.val() ? selected.text() : "";
							this.input = $("<input>")
									.appendTo(this.wrapper)
									.val(value)
									.attr("title", "")
									.addClass(
											"custom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left")
									.autocomplete({
										delay : 0,
										minLength : 0,
										source : $.proxy(this, "_source")
									}).tooltip({
										tooltipClass : "ui-state-highlight"
									});
							this._on(this.input, {
								autocompleteselect : function(event, ui) {
									ui.item.option.selected = true;
									this._trigger("select", event, {
										item : ui.item.option
									});
									showSelect(ui.item.option.value);
								},
								autocompletechange : "_removeIfInvalid"
							});
						},
						_createShowAllButton : function() {
							var input = this.input, wasOpen = false;
							$("<a>").attr("tabIndex", -1).attr("title",
									"Show All Items").tooltip().appendTo(
									this.wrapper).button({
								icons : {
									primary : "ui-icon-triangle-1-s"
								},
								text : false
							}).removeClass("ui-corner-all").addClass(
									"custom-combobox-toggle ui-corner-right")
									.mousedown(
											function() {
												wasOpen = input.autocomplete(
														"widget")
														.is(":visible");
											}).click(function() {
										input.focus();// Close if already
										// visible
										if (wasOpen) {
											return;
										}
										// Pass empty string as value to search
										// for, displaying all results
										input.autocomplete("search", "");
									});
						},
						_source : function(request, response) {
							var matcher = new RegExp($.ui.autocomplete
									.escapeRegex(request.term), "i");
							response(this.element.children("option").map(
									function() {
										var text = $(this).text();
										if (this.value
												&& (!request.term || matcher
														.test(text)))
											return {
												label : text,
												value : text,
												option : this
											};
									}));
						},
						_removeIfInvalid : function(event, ui) {
							// Selected an item, nothing to do
							if (ui.item) {
								return;
							}
							// Search for a match (case-insensitive)
							var value = this.input.val(), valueLowerCase = value
									.toLowerCase(), valid = false;
							this.element
									.children("option")
									.each(
											function() {
												if ($(this).text()
														.toLowerCase() === valueLowerCase) {
													this.selected = valid = true;
													return false;
												}
											});
							// Found a match, nothing to do
							if (valid) {
								return;
							}
							// Remove invalid value
							this.input.val("").attr("title",
									value + "nothing matched").tooltip("open");
							this.element.val("");
							this._delay(function() {
								this.input.tooltip("close").attr("title", "");
							}, 2500);
							this.input.autocomplete("instance").term = "";
						},
						_destroy : function() {
							this.wrapper.remove();
							this.element.show();

						}

					});
})(jQuery);

function showSelect(selectValue) {

	var sid = selectValue.substr(1);
	var stype = selectValue.substr(0, 1);
	var url, nextType;
	var postData;

	switch (stype) {
	case "s":
		url = "interfaces";
		postData = {
			"serviceId" : sid
		};
		nextType = "i";
		
		isImitate($("#servicescombo option:selected").text());
		break;
	case "i":
		url = "methods";
		postData = {
			"interfaceId" : sid
		};
		nextType = "m"
		break;
	case "m":
		url = "args";
		postData = {
			"methodId" : sid
		};
		nextType = "a"
		break;
	case "a":
		break;
	default:
		break;
	}
	$.ajax({
		type : "POST",
		url : "config/list-" + url,
		contentType : "application/json",
		dataType : 'json',
		data : JSON.stringify(postData),
		success : function(data) {
			
			if (nextType == "a") {
				$("#args").empty();
				
				$.each(data, function(i, v) {
					$("#args").append("<p id ='pdb"+v.confId+"'>"
							+ showArgMode(v.confId,v.argMode)
							+ showArgIdx(v.confId,v.argIdx)
							+ showArgValue(v.confId,v.argValue)
							+ showArgType(v.confId,v.argType)
							+ showAttrName(v.confId,v.attrName)
							+ showAttrType(v.confId,v.attrType)
							+ showAttrValue(v.confId,v.attrValue)
							+ showHiddenValue(v.confId,v.confId)
							+"</p>");
							
				});
				
			}else{
				$("#" + url + "combo").empty();
				$.each(data, function(k, v) {
					$("#" + url + "combo").append(
							"<option value='" + nextType + k + "'>" + v
									+ "</option>");
				});
			}
		}
	});
}

function isImitate(serviceCode){
	
	var postData = {"service":serviceCode};
	
	$.ajax({
		type : "POST",
		url : "config/isimitate",
		contentType : "application/json",
		dataType : 'json',
		data: JSON.stringify(postData),
		success : function(data) {

			if(data.isImitate){
				$("#refresh").attr("value","切换至联调");
				action = 1 ;
			}else{
				$("#refresh").attr("value","切换至模拟");
				action = 0 ;
			}
				

		}
	});
}


function showArgMode(id,value){
	return "<input id='amod" + id + "' type='text' value='"+value+"' />";
}

function showArgIdx(id,value){
	return "<input id='aidx" + id + "' type='text' value='"+value+"' />";
}

function showArgValue(id,value){
	return "<input id='aval" + id + "' type='text' value='"+value+"' />";
}

function showArgType(id,value){
	return "<input id='atyp" + id + "' type='text' value='"+value+"' />";
}

function showAttrName(id,value){
	return "<input id='attn" + id + "' type='text' value='"+value+"' />";
}

function showAttrType(id,value){
	return "<input id='attt" + id + "' type='text' value='"+value+"' />";
}

function showAttrValue(id,value){
	return "<input id='attv" + id + "' type='text' value='"+value+"' />";
}

function showHiddenValue(id,value){
	return "<input id='conf" + id + "' type='hidden' value='"+value+"' />";
}