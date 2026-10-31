package dev.bastighg;

import java.util.ArrayList;
import java.util.List;

/**
 * Alle 193 UN-Mitglieder + Vatikan, Palaestina, Kosovo, Taiwan.
 * Format: Name|Hauptstadt|Flaggen-DSL (siehe FlagPainter).
 */
public record Country(String name, String capital, String flag) {

    private static final String DATA = """
Afghanistan|Kabul|V:K,R,G/D:W:3
Ägypten|Kairo|H:R,W,K/D:Y:2
Albanien|Tirana|R:R/S:K:5
Algerien|Algier|V:G,W/D:R:4
Andorra|Andorra la Vella|V:B,Y,R
Angola|Luanda|H:R,K/D:Y:3
Antigua und Barbuda|Saint John’s|R:R/DI:K:8:15:6/DI:Y:3:15:4
Äquatorialguinea|Malabo|H:G,W,R/T:B:8
Argentinien|Buenos Aires|H:L,W,L/D:Y:2
Armenien|Eriwan|H:R,B,O
Aserbaidschan|Baku|H:L,R,G/D:W:3
Äthiopien|Addis Abeba|H:G,Y,R/D:B:4/S:Y:3
Australien|Canberra|R:N/UJ:W:15:10/S:W:3:7:15/S:W:2:22:5/S:W:2:25:11
Bahamas|Nassau|H:L,Y,L/T:K:12
Bahrain|Manama|V:W,R*4
Bangladesch|Dhaka|R:G/D:R:6:13:10
Barbados|Bridgetown|V:B,Y,B/S:K:3
Belgien|Brüssel|V:K,Y,R
Belize|Belmopan|R:B/BD:R:3/D:W:6/D:G:4
Benin|Porto-Novo|H:Y,R/LV:G:12
Bhutan|Thimphu|H:Y,O/D:W:4
Bolivien|Sucre|H:R,Y,G
Bosnien und Herzegowina|Sarajevo|R:N/DI:Y:8/S:W:2
Botswana|Gaborone|R:L/CH:W:6/CH:K:4
Brasilien|Brasília|R:G/DI:Y:9/D:N:4
Brunei|Bandar Seri Begawan|R:Y/D:R:4
Bulgarien|Sofia|H:W,G,R
Burkina Faso|Ouagadougou|H:R,G/S:Y:4
Burundi|Gitega|R:G/X:W:3/D:W:5/S:R:3
Cabo Verde|Praia|H:B*9,W,R*2,W,B*7
Chile|Santiago de Chile|H:W,R/CT:B:10:10/S:W:3:5:5
China|Peking|R:R/S:Y:4:6:6/D:Y:1:12:2/D:Y:1:14:4/D:Y:1:14:7/D:Y:1:12:9
Costa Rica|San José|H:B,W,R*2,W,B
Dänemark|Kopenhagen|R:R/NC:W:4
Deutschland|Berlin|H:K,R,Y
Dominica|Roseau|R:G/PL:Y:3/D:R:4
Dominikanische Republik|Santo Domingo|Q:B,R,R,B/PL:W:4
Dschibuti|Dschibuti|H:L,G/T:W:16/S:R:2:5:10
Ecuador|Quito|H:Y*2,B,R
Elfenbeinküste|Yamoussoukro|V:O,W,G
El Salvador|San Salvador|H:B,W,B/D:G:2
Eritrea|Asmara|H:G,B/T:R:30
Estland|Tallinn|H:B,K,W
Eswatini|Mbabane|H:B*3,Y,R*5,Y,B*3/D:K:3
Fidschi|Suva|R:L/UJ:W:15:10
Finnland|Helsinki|R:W/NC:B:4
Frankreich|Paris|V:B,W,R
Gabun|Libreville|H:G,Y,B
Gambia|Banjul|H:R*6,W,B*4,W,G*6
Georgien|Tiflis|R:W/PL:R:3
Ghana|Accra|H:R,Y,G/S:K:3
Grenada|St. George’s|R:R/DI:G:8/S:Y:2
Griechenland|Athen|H:B,W,B,W,B,W,B,W,B/CT:B:11:11/CP:W:11:11
Guatemala|Guatemala-Stadt|V:L,W,L/D:G:2
Guinea|Conakry|V:R,Y,G
Guinea-Bissau|Bissau|H:Y,G/LV:R:10/S:K:3:5:10
Guyana|Georgetown|R:G/T:Y:28/T:R:14
Haiti|Port-au-Prince|H:B,R
Honduras|Tegucigalpa|H:B,W,B/D:B:1:15:10
Indien|Neu-Delhi|H:O,W,G/D:N:3
Indonesien|Jakarta|H:R,W
Irak|Bagdad|H:R,W,K/D:G:2
Iran|Teheran|H:G,W,R/D:R:2
Irland|Dublin|V:G,W,O
Island|Reykjavík|R:B/NC:W:6/NC:R:2
Israel|Jerusalem|H:W*2,B*2,W*8,B*2,W*2/S:B:3
Italien|Rom|V:G,W,R
Jamaika|Kingston|R:K/X:Y:3
Japan|Tokio|R:W/D:R:6
Jemen|Sanaa|H:R,W,K
Jordanien|Amman|H:K,W,G/T:R:14/S:W:2:4:10
Kambodscha|Phnom Penh|H:B,R*2,B/D:W:4
Kamerun|Jaunde|V:G,R,Y/S:Y:3
Kanada|Ottawa|V:R,W,W,R/DI:R:5
Kasachstan|Astana|R:L/D:Y:4
Katar|Doha|V:W,M*4
Kenia|Nairobi|H:K*6,W,R*12,W,G*6/D:K:4
Kirgisistan|Bischkek|R:R/D:Y:5
Kiribati|Tarawa|H:R*10,B*10/D:Y:4:15:10
Kolumbien|Bogotá|H:Y*2,B,R
Komoren|Moroni|H:Y,W,R,B/T:G:10
Republik Kongo|Brazzaville|H:G,Y,R
Demokratische Republik Kongo|Kinshasa|R:L/S:Y:4:6:6
Kosovo|Priština|R:B/D:Y:4
Kroatien|Zagreb|H:R,W,B/DI:R:3
Kuba|Havanna|H:B,W,B,W,B/T:R:16/S:W:3:5:10
Kuwait|Kuwait-Stadt|H:G,W,R/T:K:6
Laos|Vientiane|H:R,B*2,R/D:W:4
Lesotho|Maseru|H:B*3,W*4,G*3/D:K:2
Lettland|Riga|H:M*2,W,M*2
Libanon|Beirut|H:R,W*2,R/D:G:3
Liberia|Monrovia|H:R,W,R,W,R,W,R,W,R,W,R/CT:B:10:11/S:W:3:5:5
Libyen|Tripolis|H:R,K*2,G/D:W:2
Liechtenstein|Vaduz|H:B,R/D:Y:2:6:5
Litauen|Wilna|H:Y,G,R
Luxemburg|Luxemburg|H:R,W,L
Madagaskar|Antananarivo|H:R,G/LV:W:10
Malawi|Lilongwe|H:K,R,G/D:R:3:15:3
Malaysia|Kuala Lumpur|H:R,W,R,W,R,W,R,W,R,W,R,W,R,W/CT:B:15:11/S:Y:3:7:5
Malediven|Malé|R:R/IN:G:5:4/D:W:3
Mali|Bamako|V:G,Y,R
Malta|Valletta|V:W,R
Marokko|Rabat|R:R/S:G:5
Marshallinseln|Majuro|R:B/S:W:4:6:6
Mauretanien|Nouakchott|R:G/BD:R:3/D:Y:4
Mauritius|Port Louis|H:R,B,Y,G
Mexiko|Mexiko-Stadt|V:G,W,R/D:O:3
Mikronesien|Palikir|R:L/S:W:2:15:10
Moldau|Chișinău|V:B,Y,R
Monaco|Monaco|H:R,W
Mongolei|Ulaanbaatar|V:R,B,R/S:Y:2:5:10
Montenegro|Podgorica|R:Y/IN:R:1:1/D:Y:4
Mosambik|Maputo|H:G*6,W,K*6,W,Y*6/T:R:14/S:Y:3:5:10
Myanmar|Naypyidaw|H:Y,G,R/S:W:5
Namibia|Windhuk|H:B,R,G/D:Y:3:5:5
Nauru|Yaren|H:B*9,Y,B*9/S:W:3:7:14
Nepal|Kathmandu|R:B/T:R:22/D:W:2:6:7
Neuseeland|Wellington|R:N/UJ:W:15:10/S:R:2:22:5/S:R:2:25:12/S:R:2:19:12
Nicaragua|Managua|H:B,W,B/D:G:2
Niederlande|Amsterdam|H:R,W,B
Niger|Niamey|H:O,W,G/D:O:3
Nigeria|Abuja|V:G,W,G
Nordkorea|Pjöngjang|H:B*4,W,R*10,W,B*4/D:W:5:10:10/S:R:4:10:10
Nordmazedonien|Skopje|R:R/D:Y:4
Norwegen|Oslo|R:R/NC:W:6/NC:B:2
Oman|Maskat|H:W,R,G/LV:R:8
Österreich|Wien|H:R,W,R
Pakistan|Islamabad|R:G/LV:W:7/D:W:4:20:10
Palästina|Ostjerusalem|H:K,W,G/T:R:12
Palau|Ngerulmud|R:L/D:Y:6:12:10
Panama|Panama-Stadt|Q:W,R,B,W/S:B:3:7:5/S:R:3:22:15
Papua-Neuguinea|Port Moresby|H:R,K/D:Y:3:22:5/S:W:3:8:14
Paraguay|Asunción|H:R,W,B/D:G:2
Peru|Lima|V:R,W,R
Philippinen|Manila|H:B,R/T:W:16/S:Y:3:5:10
Polen|Warschau|H:W,R
Portugal|Lissabon|V:G*2,R*3/D:Y:4:12:10
Ruanda|Kigali|H:L*10,Y*5,G*5/D:Y:3:24:4
Rumänien|Bukarest|V:B,Y,R
Russland|Moskau|H:W,B,R
Salomonen|Honiara|H:B,G/S:W:3:5:5
Sambia|Lusaka|R:G/S:O:3:24:6
Samoa|Apia|R:R/CT:B:15:10/S:W:2:7:5
San Marino|San Marino|H:W,L/D:Y:3
São Tomé und Príncipe|São Tomé|H:G,Y*2,G/T:R:10/S:K:3:15:10
Saudi-Arabien|Riad|R:G/CH:W:2
Schweden|Stockholm|R:B/NC:Y:4
Schweiz|Bern|R:R/PS:W
Senegal|Dakar|V:G,Y,R/S:G:3
Serbien|Belgrad|H:R,B,W
Seychellen|Victoria|H:B,Y,R,W,G
Sierra Leone|Freetown|H:G,W,B
Simbabwe|Harare|H:G,Y,R,K,R,Y,G/T:W:12/S:R:3:5:10
Singapur|Singapur|H:R,W/D:W:3:6:5
Slowakei|Bratislava|H:W,B,R/DI:R:4:9:10
Slowenien|Ljubljana|H:W,B,R/DI:B:3:7:5
Somalia|Mogadischu|R:L/S:W:5
Spanien|Madrid|H:R,Y*2,R
Sri Lanka|Sri Jayawardenepura Kotte|V:G,O,M*4/D:Y:3:19:10
St. Kitts und Nevis|Basseterre|R:G/X:Y:5/X:K:3
St. Lucia|Castries|R:L/DI:W:8/DI:K:6/DI:Y:3
St. Vincent und die Grenadinen|Kingstown|V:B,Y*2,G
Südafrika|Pretoria|H:R,B/T:G:20/T:Y:11/T:K:9
Südkorea|Seoul|R:W/D:R:5
Südsudan|Juba|H:K,R,G/T:B:12/S:Y:3:4:10
Sudan|Khartum|H:R,W,K/T:G:12
Suriname|Paramaribo|H:G,W,R*2,W,G/S:Y:4
Syrien|Damaskus|H:R,W,K/S:G:2
Tadschikistan|Duschanbe|H:R,W*2,G
Taiwan|Taipeh|R:R/CT:B:15:10/D:W:3:7:5
Tansania|Dodoma|H:G,Y,K,Y,L
Thailand|Bangkok|H:R,W,B*2,W,R
Timor-Leste|Dili|R:R/T:Y:15/T:K:10/S:W:2:4:10
Togo|Lomé|H:G,Y,G,Y,G/CT:R:11:12/S:W:3:5:6
Tonga|Nukuʻalofa|R:R/CT:W:12:10/CP:R:12:10
Trinidad und Tobago|Port of Spain|R:R/X:K:4
Tschad|N’Djamena|V:B,Y,R
Tschechien|Prag|H:W,R/T:B:15
Tunesien|Tunis|R:R/D:W:6/D:R:4/S:R:2
Türkei|Ankara|R:R/D:W:5:11:10/D:R:4:12:10/S:W:2:16:10
Turkmenistan|Aschgabat|R:G/LV:M:5
Tuvalu|Funafuti|R:L/UJ:W:15:10/S:Y:2:22:12
Uganda|Kampala|H:K,Y,R,K,Y,R/D:W:4
Ukraine|Kiew|H:B,Y
Ungarn|Budapest|H:R,W,G
Uruguay|Montevideo|H:W,B,W,B,W,B,W,B,W/CT:W:11:10/D:Y:3:5:5
Usbekistan|Taschkent|H:L*6,R,W*5,R,G*6
Vanuatu|Port Vila|H:R,G/T:Y:20/T:K:12
Vatikanstadt|Vatikanstadt|V:Y,W
Venezuela|Caracas|H:Y,B,R/S:W:2
Vereinigte Arabische Emirate|Abu Dhabi|H:G,W,K/LV:R:8
Vereinigte Staaten|Washington, D.C.|H:R,W,R,W,R,W,R,W,R,W,R,W,R/CT:N:13:11
Vereinigtes Königreich|London|UJ:W:30:20
Vietnam|Hanoi|R:R/S:Y:6
Weißrussland (Belarus)|Minsk|H:R*2,G/LV:W:3
Zentralafrikanische Republik|Bangui|H:B,W,G,Y/CV:R:6
Zypern|Nikosia|R:W/D:O:4
""";

    public static final List<Country> ALL = new ArrayList<>();

    static {
        DATA.lines().filter(l -> !l.isBlank()).forEach(l -> {
            String[] p = l.split("\\|");
            ALL.add(new Country(p[0], p[1], p[2]));
        });
    }
}
