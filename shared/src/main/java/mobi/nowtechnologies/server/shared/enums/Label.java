package mobi.nowtechnologies.server.shared.enums;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public enum Label {
	_(""),
	THREE_BEAT_AATW("3 BEAT/AATW"),
	ASYLUM_RECORDS("Asylum Records"),
	ASYLUM_BIG_BEAT("ASYLUM/BIG BEAT"),
	ATLANTIC("Atlantic"),
	BELUGA_HEIGHTS("Beluga Heights"),
	BREAKBEAT_KAOS("Breakbeat Kaos"),
	CANDLELIGHT("CANDLELIGHT"),
	CAPITOL("CAPITOL"),
	CASH_MONEY_ISLAND("CASH MONEY/ISLAND"),
	COLUMBIA("Columbia"),
	CONEHEAD_RECORDS("Conehead Records"),
	DATA_RECORDS("Data Records"),
	DEF_JAM("DEF JAM"),
	EAGLE_ROCK("EAGLE ROCK"),
	ELEKTRA("ELEKTRA"),
	EMI("EMI"),
	EPIC("EPIC"),
	GAME_SHADY_INTERSCOPE("Game, Shady, Interscope"),
	GEFFEN("Geffen"),
	GLOBAL_TALENT("Global Talent"),
	INDEPENDENT("Independent"),
	INSIDE_OUT("Inside Out"),
	INTERSCOPE("INTERSCOPE"),
	ISLAND("ISLAND"),
	ISLAND_DEF_JAM("Island Def Jam"),
	ISLAND_RECORDS("Island Records"),
	ISLAND_LAVA("ISLAND/LAVA"),
	J("J"),
	JIVE("Jive"),
	LEVELS_MINISTRY_OF_SOUND("LEVELS/MINISTRY OF SOUND"),
	MERCURY("MERCURY"),
	MERCURY_RECORDS("Mercury Records"),
	mercury_records_limited("Mercury Records Limited"),
	MINISTRY_OF_SOUND("Ministry Of Sound"),
	MOST_RADICALIST_BLACK_POLYDOR("MOST RADICALIST BLACK/POLYDOR"),
	MTA("MTA"),
	NUCLEAR_BLAST("NUCLEAR BLAST"),
	PARKWOOD_ENTERTAINMENT_COLUMBI("Parkwood Entertainment/Columbi"),
	PARLOPHONE("Parlophone"),
	PEACEVILLE("PEACEVILLE"),
	PIAS("PIAS"),
	POLYDOR("POLYDOR"),
	POSITIVA_VIRGIN("POSITIVA/VIRGIN"),
	RAMEN_RECORDS("Ramen Records"),
	RISE("RISE"),
	ROADRUNNER("ROADRUNNER"),
	ROC_A_FELLA("ROC-A-FELLA"),
	SEASON_OF_MIST("SEASON OF MIST"),
	Shine_TV_Ltd("Shine TV Ltd"),
	Sony("Sony"),
	SONY_MUSIC("SONY MUSIC"),
	SONY_RCA("Sony RCA"),
	SOUR_MASH("Sour Mash"),
	STARTIME_INTERNATIONAL("Startime International"),
	TAKEOVER_ENTERTAINMENT("Takeover Entertainment"),
	TEEPEE("TEEPEE"),
	TEN("TEN"),
	TEST("TEST"),
	ULTRA_RECORDS("Ultra Records"),
	UNIVERSAL("Universal"),
	UNIVERSAL_MUSIC("Universal Music"),
	UNIVERSAL_REPUBLIC("Universal Republic"),
	VIRGIN("VIRGIN"),
	VIRGIN_RECORDS("Virgin Records"),
	WARNER("Warner"),
	WARNER_BROS("WARNER BROS"),
	WARNER_MUSIC("WARNER MUSIC"),
	WARNERS("Warners"),
	XL("XL"),
	XL_RECORDINGS("XL RECORDINGS");
	
	private String name;
	
	Label(String name){
		this.name=name;
	}
	
	public String getName() {
		return name;
	}

}
