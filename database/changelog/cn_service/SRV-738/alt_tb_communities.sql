ALTER TABLE tb_communities ADD UNIQUE comm_U_url_param (rewriteURLParameter);
ALTER TABLE tb_communities ADD UNIQUE comm_U_url_name (name);
