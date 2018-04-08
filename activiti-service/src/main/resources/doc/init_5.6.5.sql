-- activiti 相关表结构
-- --  引擎相关表
create table act_ge_property (
  name_ varchar(64),
  value_ varchar(300),
  rev_ integer,
  primary key (name_)
) engine=innodb default charset=utf8 collate utf8_bin;

insert into act_ge_property
values ('schema.version', '5.22.0.0', 1);

insert into act_ge_property
values ('schema.history', 'create(5.22.0.0)', 1);

insert into act_ge_property
values ('next.dbid', '1', 1);

create table act_ge_bytearray (
  id_ varchar(64),
  rev_ integer,
  name_ varchar(255),
  deployment_id_ varchar(64),
  bytes_ longblob,
  generated_ tinyint,
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_re_deployment (
  id_ varchar(64),
  name_ varchar(255),
  category_ varchar(255),
  tenant_id_ varchar(255) default '',
  deploy_time_ timestamp(3) null,
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_re_model (
  id_ varchar(64) not null,
  rev_ integer,
  name_ varchar(255),
  key_ varchar(255),
  category_ varchar(255),
  create_time_ timestamp(3) null,
  last_update_time_ timestamp(3) null,
  version_ integer,
  meta_info_ varchar(4000),
  deployment_id_ varchar(64),
  editor_source_value_id_ varchar(64),
  editor_source_extra_value_id_ varchar(64),
  tenant_id_ varchar(255) default '',
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_ru_execution (
  id_ varchar(64),
  rev_ integer,
  proc_inst_id_ varchar(64),
  business_key_ varchar(255),
  parent_id_ varchar(64),
  proc_def_id_ varchar(64),
  super_exec_ varchar(64),
  act_id_ varchar(255),
  is_active_ tinyint,
  is_concurrent_ tinyint,
  is_scope_ tinyint,
  is_event_scope_ tinyint,
  suspension_state_ integer,
  cached_ent_state_ integer,
  tenant_id_ varchar(255) default '',
  name_ varchar(255),
  lock_time_ timestamp(3) null,
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_ru_job (
  id_ varchar(64) not null,
  rev_ integer,
  type_ varchar(255) not null,
  lock_exp_time_ timestamp(3) null,
  lock_owner_ varchar(255),
  exclusive_ boolean,
  execution_id_ varchar(64),
  process_instance_id_ varchar(64),
  proc_def_id_ varchar(64),
  retries_ integer,
  exception_stack_id_ varchar(64),
  exception_msg_ varchar(4000),
  duedate_ timestamp(3) null,
  repeat_ varchar(255),
  handler_type_ varchar(255),
  handler_cfg_ varchar(4000),
  tenant_id_ varchar(255) default '',
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_re_procdef (
  id_ varchar(64) not null,
  rev_ integer,
  category_ varchar(255),
  name_ varchar(255),
  key_ varchar(255) not null,
  version_ integer not null,
  deployment_id_ varchar(64),
  resource_name_ varchar(4000),
  dgrm_resource_name_ varchar(4000),
  description_ varchar(4000),
  has_start_form_key_ tinyint,
  has_graphical_notation_ tinyint,
  suspension_state_ integer,
  tenant_id_ varchar(255) default '',
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_ru_task (
  id_ varchar(64),
  rev_ integer,
  execution_id_ varchar(64),
  proc_inst_id_ varchar(64),
  proc_def_id_ varchar(64),
  name_ varchar(255),
  parent_task_id_ varchar(64),
  description_ varchar(4000),
  task_def_key_ varchar(255),
  owner_ varchar(255),
  assignee_ varchar(255),
  delegation_ varchar(64),
  priority_ integer,
  create_time_ timestamp(3) null,
  due_date_ datetime(3),
  category_ varchar(255),
  suspension_state_ integer,
  tenant_id_ varchar(255) default '',
  form_key_ varchar(255),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_ru_identitylink (
  id_ varchar(64),
  rev_ integer,
  group_id_ varchar(255),
  type_ varchar(255),
  user_id_ varchar(255),
  task_id_ varchar(64),
  proc_inst_id_ varchar(64),
  proc_def_id_ varchar(64),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_ru_variable (
  id_ varchar(64) not null,
  rev_ integer,
  type_ varchar(255) not null,
  name_ varchar(255) not null,
  execution_id_ varchar(64),
  proc_inst_id_ varchar(64),
  task_id_ varchar(64),
  bytearray_id_ varchar(64),
  double_ double,
  long_ bigint,
  text_ varchar(4000),
  text2_ varchar(4000),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_ru_event_subscr (
  id_ varchar(64) not null,
  rev_ integer,
  event_type_ varchar(255) not null,
  event_name_ varchar(255),
  execution_id_ varchar(64),
  proc_inst_id_ varchar(64),
  activity_id_ varchar(64),
  configuration_ varchar(255),
  created_ timestamp(3) not null default current_timestamp(3),
  proc_def_id_ varchar(64),
  tenant_id_ varchar(255) default '',
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_evt_log (
  log_nr_ bigint auto_increment,
  type_ varchar(64),
  proc_def_id_ varchar(64),
  proc_inst_id_ varchar(64),
  execution_id_ varchar(64),
  task_id_ varchar(64),
  time_stamp_ timestamp(3) not null,
  user_id_ varchar(255),
  data_ longblob,
  lock_owner_ varchar(255),
  lock_time_ timestamp(3) null,
  is_processed_ tinyint default 0,
  primary key (log_nr_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_procdef_info (
  id_ varchar(64) not null,
  proc_def_id_ varchar(64) not null,
  rev_ integer,
  info_json_id_ varchar(64),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create index act_idx_exec_buskey on act_ru_execution(business_key_);
create index act_idx_task_create on act_ru_task(create_time_);
create index act_idx_ident_lnk_user on act_ru_identitylink(user_id_);
create index act_idx_ident_lnk_group on act_ru_identitylink(group_id_);
create index act_idx_event_subscr_config_ on act_ru_event_subscr(configuration_);
create index act_idx_variable_task_id on act_ru_variable(task_id_);
create index act_idx_athrz_procedef on act_ru_identitylink(proc_def_id_);
create index act_idx_info_procdef on act_procdef_info(proc_def_id_);

alter table act_ge_bytearray
  add constraint act_fk_bytearr_depl
foreign key (deployment_id_)
references act_re_deployment (id_);

alter table act_re_procdef
  add constraint act_uniq_procdef
unique (key_,version_, tenant_id_);

alter table act_ru_execution
  add constraint act_fk_exe_procinst
foreign key (proc_inst_id_)
references act_ru_execution (id_) on delete cascade on update cascade;

alter table act_ru_execution
  add constraint act_fk_exe_parent
foreign key (parent_id_)
references act_ru_execution (id_);

alter table act_ru_execution
  add constraint act_fk_exe_super
foreign key (super_exec_)
references act_ru_execution (id_);

alter table act_ru_execution
  add constraint act_fk_exe_procdef
foreign key (proc_def_id_)
references act_re_procdef (id_);

alter table act_ru_identitylink
  add constraint act_fk_tskass_task
foreign key (task_id_)
references act_ru_task (id_);

alter table act_ru_identitylink
  add constraint act_fk_athrz_procedef
foreign key (proc_def_id_)
references act_re_procdef(id_);

alter table act_ru_identitylink
  add constraint act_fk_idl_procinst
foreign key (proc_inst_id_)
references act_ru_execution (id_);

alter table act_ru_task
  add constraint act_fk_task_exe
foreign key (execution_id_)
references act_ru_execution (id_);

alter table act_ru_task
  add constraint act_fk_task_procinst
foreign key (proc_inst_id_)
references act_ru_execution (id_);

alter table act_ru_task
  add constraint act_fk_task_procdef
foreign key (proc_def_id_)
references act_re_procdef (id_);

alter table act_ru_variable
  add constraint act_fk_var_exe
foreign key (execution_id_)
references act_ru_execution (id_);

alter table act_ru_variable
  add constraint act_fk_var_procinst
foreign key (proc_inst_id_)
references act_ru_execution(id_);

alter table act_ru_variable
  add constraint act_fk_var_bytearray
foreign key (bytearray_id_)
references act_ge_bytearray (id_);

alter table act_ru_job
  add constraint act_fk_job_exception
foreign key (exception_stack_id_)
references act_ge_bytearray (id_);

alter table act_ru_event_subscr
  add constraint act_fk_event_exec
foreign key (execution_id_)
references act_ru_execution(id_);

alter table act_re_model
  add constraint act_fk_model_source
foreign key (editor_source_value_id_)
references act_ge_bytearray (id_);

alter table act_re_model
  add constraint act_fk_model_source_extra
foreign key (editor_source_extra_value_id_)
references act_ge_bytearray (id_);

alter table act_re_model
  add constraint act_fk_model_deployment
foreign key (deployment_id_)
references act_re_deployment (id_);

alter table act_procdef_info
  add constraint act_fk_info_json_ba
foreign key (info_json_id_)
references act_ge_bytearray (id_);

alter table act_procdef_info
  add constraint act_fk_info_procdef
foreign key (proc_def_id_)
references act_re_procdef (id_);

alter table act_procdef_info
  add constraint act_uniq_info_procdef
unique (proc_def_id_);


-- 历史记录表

create table act_hi_procinst (
  id_ varchar(64) not null,
  proc_inst_id_ varchar(64) not null,
  business_key_ varchar(255),
  proc_def_id_ varchar(64) not null,
  start_time_ datetime(3) not null,
  end_time_ datetime(3),
  duration_ bigint,
  start_user_id_ varchar(255),
  start_act_id_ varchar(255),
  end_act_id_ varchar(255),
  super_process_instance_id_ varchar(64),
  delete_reason_ varchar(4000),
  tenant_id_ varchar(255) default '',
  name_ varchar(255),
  primary key (id_),
  unique (proc_inst_id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_hi_actinst (
  id_ varchar(64) not null,
  proc_def_id_ varchar(64) not null,
  proc_inst_id_ varchar(64) not null,
  execution_id_ varchar(64) not null,
  act_id_ varchar(255) not null,
  task_id_ varchar(64),
  call_proc_inst_id_ varchar(64),
  act_name_ varchar(255),
  act_type_ varchar(255) not null,
  assignee_ varchar(255),
  start_time_ datetime(3) not null,
  end_time_ datetime(3),
  duration_ bigint,
  tenant_id_ varchar(255) default '',
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_hi_taskinst (
  id_ varchar(64) not null,
  proc_def_id_ varchar(64),
  task_def_key_ varchar(255),
  proc_inst_id_ varchar(64),
  execution_id_ varchar(64),
  name_ varchar(255),
  parent_task_id_ varchar(64),
  description_ varchar(4000),
  owner_ varchar(255),
  assignee_ varchar(255),
  start_time_ datetime(3) not null,
  claim_time_ datetime(3),
  end_time_ datetime(3),
  duration_ bigint,
  delete_reason_ varchar(4000),
  priority_ integer,
  due_date_ datetime(3),
  form_key_ varchar(255),
  category_ varchar(255),
  tenant_id_ varchar(255) default '',
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_hi_varinst (
  id_ varchar(64) not null,
  proc_inst_id_ varchar(64),
  execution_id_ varchar(64),
  task_id_ varchar(64),
  name_ varchar(255) not null,
  var_type_ varchar(100),
  rev_ integer,
  bytearray_id_ varchar(64),
  double_ double,
  long_ bigint,
  text_ varchar(4000),
  text2_ varchar(4000),
  create_time_ datetime(3),
  last_updated_time_ datetime(3),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_hi_detail (
  id_ varchar(64) not null,
  type_ varchar(255) not null,
  proc_inst_id_ varchar(64),
  execution_id_ varchar(64),
  task_id_ varchar(64),
  act_inst_id_ varchar(64),
  name_ varchar(255) not null,
  var_type_ varchar(255),
  rev_ integer,
  time_ datetime(3) not null,
  bytearray_id_ varchar(64),
  double_ double,
  long_ bigint,
  text_ varchar(4000),
  text2_ varchar(4000),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_hi_comment (
  id_ varchar(64) not null,
  type_ varchar(255),
  time_ datetime(3) not null,
  user_id_ varchar(255),
  task_id_ varchar(64),
  proc_inst_id_ varchar(64),
  action_ varchar(255),
  message_ varchar(4000),
  full_msg_ longblob,
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_hi_attachment (
  id_ varchar(64) not null,
  rev_ integer,
  user_id_ varchar(255),
  name_ varchar(255),
  description_ varchar(4000),
  type_ varchar(255),
  task_id_ varchar(64),
  proc_inst_id_ varchar(64),
  url_ varchar(4000),
  content_id_ varchar(64),
  time_ datetime(3),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_hi_identitylink (
  id_ varchar(64),
  group_id_ varchar(255),
  type_ varchar(255),
  user_id_ varchar(255),
  task_id_ varchar(64),
  proc_inst_id_ varchar(64),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;


create index act_idx_hi_pro_inst_end on act_hi_procinst(end_time_);
create index act_idx_hi_pro_i_buskey on act_hi_procinst(business_key_);
create index act_idx_hi_act_inst_start on act_hi_actinst(start_time_);
create index act_idx_hi_act_inst_end on act_hi_actinst(end_time_);
create index act_idx_hi_detail_proc_inst on act_hi_detail(proc_inst_id_);
create index act_idx_hi_detail_act_inst on act_hi_detail(act_inst_id_);
create index act_idx_hi_detail_time on act_hi_detail(time_);
create index act_idx_hi_detail_name on act_hi_detail(name_);
create index act_idx_hi_detail_task_id on act_hi_detail(task_id_);
create index act_idx_hi_procvar_proc_inst on act_hi_varinst(proc_inst_id_);
create index act_idx_hi_procvar_name_type on act_hi_varinst(name_, var_type_);
create index act_idx_hi_procvar_task_id on act_hi_varinst(task_id_);
create index act_idx_hi_act_inst_procinst on act_hi_actinst(proc_inst_id_, act_id_);
create index act_idx_hi_act_inst_exec on act_hi_actinst(execution_id_, act_id_);
create index act_idx_hi_ident_lnk_user on act_hi_identitylink(user_id_);
create index act_idx_hi_ident_lnk_task on act_hi_identitylink(task_id_);
create index act_idx_hi_ident_lnk_procinst on act_hi_identitylink(proc_inst_id_);
create index act_idx_hi_task_inst_procinst on act_hi_taskinst(proc_inst_id_);

--  用户组相关表

create table act_id_group (
  id_ varchar(64),
  rev_ integer,
  name_ varchar(255),
  type_ varchar(255),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_id_membership (
  user_id_ varchar(64),
  group_id_ varchar(64),
  primary key (user_id_, group_id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_id_user (
  id_ varchar(64),
  rev_ integer,
  first_ varchar(255),
  last_ varchar(255),
  email_ varchar(255),
  pwd_ varchar(255),
  picture_id_ varchar(64),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table act_id_info (
  id_ varchar(64),
  rev_ integer,
  user_id_ varchar(64),
  type_ varchar(64),
  key_ varchar(255),
  value_ varchar(255),
  password_ longblob,
  parent_id_ varchar(255),
  primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

alter table act_id_membership
  add constraint act_fk_memb_group
foreign key (group_id_)
references act_id_group (id_);

alter table act_id_membership
  add constraint act_fk_memb_user
foreign key (user_id_)
references act_id_user (id_);




