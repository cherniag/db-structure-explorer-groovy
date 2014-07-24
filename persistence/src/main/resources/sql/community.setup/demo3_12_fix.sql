set autocommit=0;
start transaction;

use cn_service;

update tb_charts set name='BASIC_CHART_1 - DEMO3', type='BASIC_CHART'
where name='OTHER_CHART_1 - DEMO3' and type='OTHER_CHART';
update tb_charts set name='BASIC_CHART_7 - DEMO3', type='BASIC_CHART'
where name='OTHER_CHART_7 - DEMO3' and type='OTHER_CHART';

update tb_charts set name='OTHER_CHART_2 - DEMO3', type='OTHER_CHART'
where name='BASIC_CHART_2 - DEMO3' and type='BASIC_CHART';
update tb_charts set name='OTHER_CHART_3 - DEMO3', type='OTHER_CHART'
where name='BASIC_CHART_3 - DEMO3' and type='BASIC_CHART';
update tb_charts set name='OTHER_CHART_4 - DEMO3', type='OTHER_CHART'
where name='BASIC_CHART_4 - DEMO3' and type='BASIC_CHART';
update tb_charts set name='OTHER_CHART_5 - DEMO3', type='OTHER_CHART'
where name='BASIC_CHART_5 - DEMO3' and type='BASIC_CHART';
update tb_charts set name='OTHER_CHART_6 - DEMO3', type='OTHER_CHART'
where name='BASIC_CHART_6 - DEMO3' and type='BASIC_CHART';
update tb_charts set name='OTHER_CHART_8 - DEMO3', type='OTHER_CHART'
where name='BASIC_CHART_8 - DEMO3' and type='BASIC_CHART';
update tb_charts set name='OTHER_CHART_9 - DEMO3', type='OTHER_CHART'
where name='BASIC_CHART_9 - DEMO3' and type='BASIC_CHART';
update tb_charts set name='OTHER_CHART_10 - DEMO3', type='OTHER_CHART'
where name='BASIC_CHART_10 - DEMO3' and type='BASIC_CHART';
update tb_charts set name='OTHER_CHART_11 - DEMO3', type='OTHER_CHART'
where name='BASIC_CHART_11 - DEMO3' and type='BASIC_CHART';
update tb_charts set name='OTHER_CHART_12 - DEMO3', type='OTHER_CHART'
where name='BASIC_CHART_12 - DEMO3' and type='BASIC_CHART';


commit;
