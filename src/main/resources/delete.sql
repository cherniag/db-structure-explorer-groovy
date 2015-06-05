delete `tb_submittedPayments` from `tb_submittedPayments` join `tb_paymentDetails` on `tb_submittedPayments`.paymentDetailsId = `tb_paymentDetails`.i join `tb_users` on `tb_paymentDetails`.owner_id = `tb_users`.i where tb_users.i = ?;
update `tb_users` set `tb_users`.currentPaymentDetailsId = NULL where tb_users.i = ?;
update `tb_users` set `tb_users`.last_successful_payment_details_id = NULL where tb_users.i = ?;
delete `tb_paymentDetails` from `tb_paymentDetails` join `tb_users` on `tb_paymentDetails`.owner_id = `tb_users`.i where tb_users.i = ?;
delete `bkp_tb_accountLog_srv-423` from `bkp_tb_accountLog_srv-423` join `tb_users` on `bkp_tb_accountLog_srv-423`.userUID = `tb_users`.i where tb_users.i = ?;
delete `bkp_tb_submittedPayments_srv-423` from `bkp_tb_submittedPayments_srv-423` join `tb_users` on `bkp_tb_submittedPayments_srv-423`.userId = `tb_users`.i where tb_users.i = ?;
delete `device_user_data` from `device_user_data` join `tb_users` on `device_user_data`.user_id = `tb_users`.i where tb_users.i = ?;
delete `oldpmt` from `oldpmt` join `tb_users` on `oldpmt`.userUID = `tb_users`.i where tb_users.i = ?;
delete `tb_accountLog` from `tb_accountLog` join `tb_users` on `tb_accountLog`.userUID = `tb_users`.i where tb_users.i = ?;
delete `tb_mediaLog` from `tb_mediaLog` join `tb_users` on `tb_mediaLog`.userUID = `tb_users`.i where tb_users.i = ?;
delete `tb_mediaLogArchive` from `tb_mediaLogArchive` join `tb_users` on `tb_mediaLogArchive`.userUID = `tb_users`.i where tb_users.i = ?;
delete `tb_payments` from `tb_payments` join `tb_users` on `tb_payments`.userUID = `tb_users`.i where tb_users.i = ?;
delete `tb_submittedPayments` from `tb_submittedPayments` join `tb_users` on `tb_submittedPayments`.userId = `tb_users`.i where tb_users.i = ?;
delete `tb_userAndroidDetails` from `tb_userAndroidDetails` join `tb_users` on `tb_userAndroidDetails`.userUID = `tb_users`.i where tb_users.i = ?;
delete `tb_useriPhoneDetails` from `tb_useriPhoneDetails` join `tb_users` on `tb_useriPhoneDetails`.userUID = `tb_users`.i where tb_users.i = ?;
delete `topupFix` from `topupFix` join `tb_users` on `topupFix`.useruid = `tb_users`.i where tb_users.i = ?;
delete `tt_fix` from `tt_fix` join `tb_users` on `tt_fix`.useruid = `tb_users`.i where tb_users.i = ?;
delete `tb_users` from `tb_users` where tb_users.i = ?;