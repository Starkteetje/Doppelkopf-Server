$( function() {
	$("#datepicker").datepicker({
		defaultDate: +7,
		clearText: 'löschen', clearStatus: 'aktuelles Datum löschen',
		closeText: 'schließen',
		closeStatus: 'ohne Änderungen schließen',
		prevText: '<zurück',
		prevStatus: 'letzten Monat zeigen',
		nextText: 'Vor>',
		nextStatus: 'nächsten Monat zeigen',
		currentText: 'heute',
		currentStatus: '',
		monthNames: ['Januar','Februar','März','April','Mai','Juni',
			'Juli','August','September','Oktober','November','Dezember'],
			monthNamesShort: ['Jan','Feb','März','Apr','Mai','Jun',
				'Jul','Aug','Sep','Okt','Nov','Dez'],
				monthStatus: 'anderen Monat anzeigen',
				yearStatus: 'anderes Jahr anzeigen',
				weekHeader: 'Wo',
				weekStatus: 'Woche des Monats',
				dayNames: ['Sonntag','Montag','Dienstag','Mittwoch','Donnerstag','Freitag','Samstag'],
				dayNamesShort: ['So','Mo','Di','Mi','Do','Fr','Sa'],
				dayNamesMin: ['So','Mo','Di','Mi','Do','Fr','Sa'],
				dayStatus: 'Setze DD als ersten Wochentag',
				dateStatus: 'Wähle D, M d',
				dateFormat: "DD' 'dd.mm.yy",
				firstDay: 1,
				initStatus: 'Wähle ein Datum',
				isRTL: false
	});
	if (new Date().getHours() < 15) {
		$("#datepicker").datepicker().datepicker("setDate", '-1d');
	} else {
		$("#datepicker").datepicker().datepicker("setDate", '0d');
	}
} );