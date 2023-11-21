---
-- #%L
-- YourRents GeoData Service
-- %%
-- Copyright (C) 2023 Your Rents Team
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
---
--- YourRents Geodata: global initial data insertion
---

COPY yrs_geodata.continent (id, code, name) FROM stdin;
1	AF	Africa
2	AN	Antarctica
3	AS	Asia
4	EU	Europe
5	NA	North America
6	OC	Oceania
7	SA	South America
\.

SELECT pg_catalog.setval('yrs_geodata.continent_id_seq', 7, true);

COPY yrs_geodata.country (id, iso_code, english_full_name, iso_3, local_name, number, continent_id) FROM stdin;
1	AD	Principality of Andorra	AND	Andorra	20	4
2	AE	United Arab Emirates	ARE	United Arab Emirates	784	3
3	AF	Islamic Republic of Afghanistan	AFG	Afghanistan	4	3
4	AG	Antigua and Barbuda	ATG	Antigua and Barbuda	28	5
5	AI	Anguilla	AIA	Anguilla	660	5
6	AL	Republic of Albania	ALB	Albania	8	4
7	AM	Republic of Armenia	ARM	Armenia	51	3
8	AN	Netherlands Antilles	ANT	Netherlands Antilles	530	5
9	AO	Republic of Angola	AGO	Angola	24	1
10	AQ	Antarctica (the territory South of 60 deg S)	ATA	Antarctica	10	2
11	AR	Argentine Republic	ARG	Argentina	32	7
12	AS	American Samoa	ASM	American Samoa	16	6
13	AT	Republic of Austria	AUT	Austria	40	4
14	AU	Commonwealth of Australia	AUS	Australia	36	6
15	AW	Aruba	ABW	Aruba	533	5
16	AX	Åland Islands	ALA	Åland	248	4
17	AZ	Republic of Azerbaijan	AZE	Azerbaijan	31	3
18	BA	Bosnia and Herzegovina	BIH	Bosnia and Herzegovina	70	4
19	BB	Barbados	BRB	Barbados	52	5
20	BD	People's Republic of Bangladesh	BGD	Bangladesh	50	3
21	BE	Kingdom of Belgium	BEL	Belgium	56	4
22	BF	Burkina Faso	BFA	Burkina Faso	854	1
23	BG	Republic of Bulgaria	BGR	Bulgaria	100	4
24	BH	Kingdom of Bahrain	BHR	Bahrain	48	3
25	BI	Republic of Burundi	BDI	Burundi	108	1
26	BJ	Republic of Benin	BEN	Benin	204	1
27	BL	Saint Barthelemy	BLM	Saint Barthélemy	652	5
28	BM	Bermuda	BMU	Bermuda	60	5
29	BN	Brunei Darussalam	BRN	Brunei Darussalam	96	3
30	BO	Republic of Bolivia	BOL	Bolivia	68	7
31	BR	Federative Republic of Brazil	BRA	Brazil	76	7
32	BS	Commonwealth of the Bahamas	BHS	Bahamas	44	5
33	BT	Kingdom of Bhutan	BTN	Bhutan	64	3
34	BV	Bouvet Island (Bouvetoya)	BVT	Bouvet Island	74	2
35	BW	Republic of Botswana	BWA	Botswana	72	1
36	BY	Republic of Belarus	BLR	Belarus	112	4
37	BZ	Belize	BLZ	Belize	84	5
38	CA	Canada	CAN	Canada	124	5
39	CC	Cocos (Keeling) Islands	CCK	Cocos (Keeling) Islands	166	3
40	CD	Democratic Republic of the Congo	COD	Congo (Kinshasa)	180	1
41	CF	Central African Republic	CAF	Central African Republic	140	1
42	CG	Republic of the Congo	COG	Congo (Brazzaville)	178	1
43	CH	Swiss Confederation	CHE	Switzerland	756	4
44	CI	Republic of Cote d'Ivoire	CIV	Côte d'Ivoire	384	1
45	CK	Cook Islands	COK	Cook Islands	184	6
46	CL	Republic of Chile	CHL	Chile	152	7
47	CM	Republic of Cameroon	CMR	Cameroon	120	1
48	CN	People's Republic of China	CHN	China	156	3
49	CO	Republic of Colombia	COL	Colombia	170	7
50	CR	Republic of Costa Rica	CRI	Costa Rica	188	5
51	CU	Republic of Cuba	CUB	Cuba	192	5
52	CV	Republic of Cape Verde	CPV	Cape Verde	132	1
53	CX	Christmas Island	CXR	Christmas Island	162	3
54	CY	Republic of Cyprus	CYP	Cyprus	196	3
55	CZ	Czech Republic	CZE	Czech Republic	203	4
56	DE	Federal Republic of Germany	DEU	Germany	276	4
57	DJ	Republic of Djibouti	DJI	Djibouti	262	1
58	DK	Kingdom of Denmark	DNK	Denmark	208	4
59	DM	Commonwealth of Dominica	DMA	Dominica	212	5
60	DO	Dominican Republic	DOM	Dominican Republic	214	5
61	DZ	People's Democratic Republic of Algeria	DZA	Algeria	12	1
62	EC	Republic of Ecuador	ECU	Ecuador	218	7
63	EE	Republic of Estonia	EST	Estonia	233	4
64	EG	Arab Republic of Egypt	EGY	Egypt	818	1
65	EH	Western Sahara	ESH	Western Sahara	732	1
66	ER	State of Eritrea	ERI	Eritrea	232	1
67	ES	Kingdom of Spain	ESP	Spain	724	4
68	ET	Federal Democratic Republic of Ethiopia	ETH	Ethiopia	231	1
69	FI	Republic of Finland	FIN	Finland	246	4
70	FJ	Republic of the Fiji Islands	FJI	Fiji	242	6
71	FK	Falkland Islands (Malvinas)	FLK	Falkland Islands	238	7
72	FM	Federated States of Micronesia	FSM	Micronesia	583	6
73	FO	Faroe Islands	FRO	Faroe Islands	234	4
74	FR	French Republic	FRA	France	250	4
75	GA	Gabonese Republic	GAB	Gabon	266	1
76	GB	United Kingdom of Great Britain & Northern Ireland	GBR	United Kingdom	826	4
77	GD	Grenada	GRD	Grenada	308	5
78	GE	Georgia	GEO	Georgia	268	3
79	GF	French Guiana	GUF	French Guiana	254	7
80	GG	Bailiwick of Guernsey	GGY	Guernsey	831	4
81	GH	Republic of Ghana	GHA	Ghana	288	1
82	GI	Gibraltar	GIB	Gibraltar	292	4
83	GL	Greenland	GRL	Greenland	304	5
84	GM	Republic of the Gambia	GMB	Gambia	270	1
85	GN	Republic of Guinea	GIN	Guinea	324	1
86	GP	Guadeloupe	GLP	Guadeloupe	312	5
87	GQ	Republic of Equatorial Guinea	GNQ	Equatorial Guinea	226	1
88	GR	Hellenic Republic Greece	GRC	Greece	300	4
89	GS	South Georgia and the South Sandwich Islands	SGS	South Georgia and South Sandwich Islands	239	2
90	GT	Republic of Guatemala	GTM	Guatemala	320	5
91	GU	Guam	GUM	Guam	316	6
92	GW	Republic of Guinea-Bissau	GNB	Guinea-Bissau	624	1
93	GY	Co-operative Republic of Guyana	GUY	Guyana	328	7
188	RO	Romania	ROU	Romania	642	4
94	HK	Hong Kong Special Administrative Region of China	HKG	Hong Kong	344	3
95	HM	Heard Island and McDonald Islands	HMD	Heard and McDonald Islands	334	2
96	HN	Republic of Honduras	HND	Honduras	340	5
97	HR	Republic of Croatia	HRV	Croatia	191	4
98	HT	Republic of Haiti	HTI	Haiti	332	5
99	HU	Republic of Hungary	HUN	Hungary	348	4
100	ID	Republic of Indonesia	IDN	Indonesia	360	3
101	IE	Ireland	IRL	Ireland	372	4
102	IL	State of Israel	ISR	Israel	376	3
103	IM	Isle of Man	IMN	Isle of Man	833	4
104	IN	Republic of India	IND	India	356	3
105	IO	British Indian Ocean Territory (Chagos Archipelago)	IOT	British Indian Ocean Territory	86	3
106	IQ	Republic of Iraq	IRQ	Iraq	368	3
107	IR	Islamic Republic of Iran	IRN	Iran	364	3
108	IS	Republic of Iceland	ISL	Iceland	352	4
109	IT	Italian Republic	ITA	Italy	380	4
110	JE	Bailiwick of Jersey	JEY	Jersey	832	4
111	JM	Jamaica	JAM	Jamaica	388	5
112	JO	Hashemite Kingdom of Jordan	JOR	Jordan	400	3
113	JP	Japan	JPN	Japan	392	3
114	KE	Republic of Kenya	KEN	Kenya	404	1
115	KG	Kyrgyz Republic	KGZ	Kyrgyzstan	417	3
116	KH	Kingdom of Cambodia	KHM	Cambodia	116	3
117	KI	Republic of Kiribati	KIR	Kiribati	296	6
118	KM	Union of the Comoros	COM	Comoros	174	1
119	KN	Federation of Saint Kitts and Nevis	KNA	Saint Kitts and Nevis	659	5
120	KP	Democratic People's Republic of Korea	PRK	Korea, North	408	3
121	KR	Republic of Korea	KOR	Korea, South	410	3
122	KW	State of Kuwait	KWT	Kuwait	414	3
123	KY	Cayman Islands	CYM	Cayman Islands	136	5
124	KZ	Republic of Kazakhstan	KAZ	Kazakhstan	398	3
125	LA	Lao People's Democratic Republic	LAO	Laos	418	3
126	LB	Lebanese Republic	LBN	Lebanon	422	3
127	LC	Saint Lucia	LCA	Saint Lucia	662	5
128	LI	Principality of Liechtenstein	LIE	Liechtenstein	438	4
129	LK	Democratic Socialist Republic of Sri Lanka	LKA	Sri Lanka	144	3
130	LR	Republic of Liberia	LBR	Liberia	430	1
131	LS	Kingdom of Lesotho	LSO	Lesotho	426	1
132	LT	Republic of Lithuania	LTU	Lithuania	440	4
133	LU	Grand Duchy of Luxembourg	LUX	Luxembourg	442	4
134	LV	Republic of Latvia	LVA	Latvia	428	4
135	LY	Libyan Arab Jamahiriya	LBY	Libya	434	1
136	MA	Kingdom of Morocco	MAR	Morocco	504	1
137	MC	Principality of Monaco	MCO	Monaco	492	4
138	MD	Republic of Moldova	MDA	Moldova	498	4
139	ME	Republic of Montenegro	MNE	Montenegro	499	4
140	MF	Saint Martin	MAF	Saint Martin (French part)	663	5
141	MG	Republic of Madagascar	MDG	Madagascar	450	1
142	MH	Republic of the Marshall Islands	MHL	Marshall Islands	584	6
143	MK	Republic of Macedonia	MKD	Macedonia	807	4
144	ML	Republic of Mali	MLI	Mali	466	1
145	MM	Union of Myanmar	MMR	Myanmar	104	3
146	MN	Mongolia	MNG	Mongolia	496	3
147	MO	Macao Special Administrative Region of China	MAC	Macau	446	3
148	MP	Commonwealth of the Northern Mariana Islands	MNP	Northern Mariana Islands	580	6
149	MQ	Martinique	MTQ	Martinique	474	5
150	MR	Islamic Republic of Mauritania	MRT	Mauritania	478	1
151	MS	Montserrat	MSR	Montserrat	500	5
152	MT	Republic of Malta	MLT	Malta	470	4
153	MU	Republic of Mauritius	MUS	Mauritius	480	1
154	MV	Republic of Maldives	MDV	Maldives	462	3
155	MW	Republic of Malawi	MWI	Malawi	454	1
156	MX	United Mexican States	MEX	Mexico	484	5
157	MY	Malaysia	MYS	Malaysia	458	3
158	MZ	Republic of Mozambique	MOZ	Mozambique	508	1
159	NA	Republic of Namibia	NAM	Namibia	516	1
160	NC	New Caledonia	NCL	New Caledonia	540	6
161	NE	Republic of Niger	NER	Niger	562	1
162	NF	Norfolk Island	NFK	Norfolk Island	574	6
163	NG	Federal Republic of Nigeria	NGA	Nigeria	566	1
164	NI	Republic of Nicaragua	NIC	Nicaragua	558	5
165	NL	Kingdom of the Netherlands	NLD	Netherlands	528	4
166	NO	Kingdom of Norway	NOR	Norway	578	4
167	NP	State of Nepal	NPL	Nepal	524	3
168	NR	Republic of Nauru	NRU	Nauru	520	6
169	NU	Niue	NIU	Niue	570	6
170	NZ	New Zealand	NZL	New Zealand	554	6
171	OM	Sultanate of Oman	OMN	Oman	512	3
172	PA	Republic of Panama	PAN	Panama	591	5
173	PE	Republic of Peru	PER	Peru	604	7
174	PF	French Polynesia	PYF	French Polynesia	258	6
175	PG	Independent State of Papua New Guinea	PNG	Papua New Guinea	598	6
176	PH	Republic of the Philippines	PHL	Philippines	608	3
177	PK	Islamic Republic of Pakistan	PAK	Pakistan	586	3
178	PL	Republic of Poland	POL	Poland	616	4
179	PM	Saint Pierre and Miquelon	SPM	Saint Pierre and Miquelon	666	5
180	PN	Pitcairn Islands	PCN	Pitcairn	612	6
181	PR	Commonwealth of Puerto Rico	PRI	Puerto Rico	630	5
182	PS	Occupied Palestinian Territory	PSE	Palestine	275	3
183	PT	Portuguese Republic	PRT	Portugal	620	4
184	PW	Republic of Palau	PLW	Palau	585	6
185	PY	Republic of Paraguay	PRY	Paraguay	600	7
186	QA	State of Qatar	QAT	Qatar	634	3
187	RE	Reunion	REU	Reunion	638	1
189	RS	Republic of Serbia	SRB	Serbia	688	4
190	RU	Russian Federation	RUS	Russian Federation	643	4
191	RW	Republic of Rwanda	RWA	Rwanda	646	1
192	SA	Kingdom of Saudi Arabia	SAU	Saudi Arabia	682	3
193	SB	Solomon Islands	SLB	Solomon Islands	90	6
194	SC	Republic of Seychelles	SYC	Seychelles	690	1
195	SD	Republic of Sudan	SDN	Sudan	736	1
196	SE	Kingdom of Sweden	SWE	Sweden	752	4
197	SG	Republic of Singapore	SGP	Singapore	702	3
198	SH	Saint Helena	SHN	Saint Helena	654	1
199	SI	Republic of Slovenia	SVN	Slovenia	705	4
200	SJ	Svalbard & Jan Mayen Islands	SJM	Svalbard and Jan Mayen Islands	744	4
201	SK	Slovakia (Slovak Republic)	SVK	Slovakia	703	4
202	SL	Republic of Sierra Leone	SLE	Sierra Leone	694	1
203	SM	Republic of San Marino	SMR	San Marino	674	4
204	SN	Republic of Senegal	SEN	Senegal	686	1
205	SO	Somali Republic	SOM	Somalia	706	1
206	SR	Republic of Suriname	SUR	Suriname	740	7
207	ST	Democratic Republic of Sao Tome and Principe	STP	Sao Tome and Principe	678	1
208	SV	Republic of El Salvador	SLV	El Salvador	222	5
209	SY	Syrian Arab Republic	SYR	Syria	760	3
210	SZ	Kingdom of Swaziland	SWZ	Swaziland	748	1
211	TC	Turks and Caicos Islands	TCA	Turks and Caicos Islands	796	5
212	TD	Republic of Chad	TCD	Chad	148	1
213	TF	French Southern Territories	ATF	French Southern Lands	260	2
214	TG	Togolese Republic	TGO	Togo	768	1
215	TH	Kingdom of Thailand	THA	Thailand	764	3
216	TJ	Republic of Tajikistan	TJK	Tajikistan	762	3
217	TK	Tokelau	TKL	Tokelau	772	6
218	TL	Democratic Republic of Timor-Leste	TLS	Timor-Leste	626	3
219	TM	Turkmenistan	TKM	Turkmenistan	795	3
220	TN	Tunisian Republic	TUN	Tunisia	788	1
221	TO	Kingdom of Tonga	TON	Tonga	776	6
222	TR	Republic of Turkey	TUR	Turkey	792	3
223	TT	Republic of Trinidad and Tobago	TTO	Trinidad and Tobago	780	5
224	TV	Tuvalu	TUV	Tuvalu	798	6
225	TW	Taiwan	TWN	Taiwan	158	3
226	TZ	United Republic of Tanzania	TZA	Tanzania	834	1
227	UA	Ukraine	UKR	Ukraine	804	4
228	UG	Republic of Uganda	UGA	Uganda	800	1
229	UM	United States Minor Outlying Islands	UMI	United States Minor Outlying Islands	581	6
230	US	United States of America	USA	United States of America	840	5
231	UY	Eastern Republic of Uruguay	URY	Uruguay	858	7
232	UZ	Republic of Uzbekistan	UZB	Uzbekistan	860	3
233	VA	Holy See (Vatican City State)	VAT	Vatican City	336	4
234	VC	Saint Vincent and the Grenadines	VCT	Saint Vincent and the Grenadines	670	5
235	VE	Bolivarian Republic of Venezuela	VEN	Venezuela	862	7
236	VG	British Virgin Islands	VGB	Virgin Islands, British	92	5
237	VI	United States Virgin Islands	VIR	Virgin Islands, U.S.	850	5
238	VN	Socialist Republic of Vietnam	VNM	Vietnam	704	3
239	VU	Republic of Vanuatu	VUT	Vanuatu	548	6
240	WF	Wallis and Futuna	WLF	Wallis and Futuna Islands	876	6
241	WS	Independent State of Samoa	WSM	Samoa	882	6
242	YE	Yemen	YEM	Yemen	887	3
243	YT	Mayotte	MYT	Mayotte	175	1
244	ZA	Republic of South Africa	ZAF	South Africa	710	1
245	ZM	Republic of Zambia	ZMB	Zambia	894	1
246	ZW	Republic of Zimbabwe	ZWE	Zimbabwe	716	1
\.

SELECT pg_catalog.setval('yrs_geodata.country_id_seq', 246, true);
