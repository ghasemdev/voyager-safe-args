package com.parsuomash.sample.ui.screens.sample

import androidx.compose.runtime.Composable
import com.parsuomash.voyager_safe_args.annotation.Visualization

@Visualization(
  name = "Register",
  graph = "Login",
  destinations = ["LoginOTP"],
  isOptional = true,
  isStart = true
)
@Composable
fun LoginRegister() {
}

@Visualization(
  name = "Register",
  graph = "IssueCertificate",
  destinations = ["x->IssueOTP"],
  isOptional = true
)
@Composable
fun IssueRegister() {
}

@Visualization(
  name = "OTP",
  graph = "Login",
  destinations = ["x->Dashboard"],
)
@Composable
fun LoginOTP() {
}

@Visualization(
  name = "OTP",
  graph = "IssueCertificate",
  destinations = ["x->Farashenasa"],
  isOptional = true
)
@Composable
fun IssueOTP() {
}

@Visualization(
  name = "OTP",
  graph = "RevokeCertificate",
  destinations = ["x->Certificates"]
)
@Composable
fun RevokeOTP() {
}

@Visualization(
  graph = "IssueCertificate",
  destinations = ["x->IssueRegister"],
  isOptional = true
)
@Composable
fun Term() {
}

@Visualization(
  graph = "IssueCertificate",
  destinations = ["x->Payment"],
  isOptional = true
)
@Composable
fun Farashenasa() {
}

@Visualization(
  graph = "IssueCertificate",
  destinations = ["x->Issue"],
  isOptional = true
)
@Composable
fun Payment() {
}

@Visualization(
  graph = "IssueCertificate",
  destinations = ["x->Dashboard"],
  isEnd = true
)
@Composable
fun Issue() {
}

@Visualization(
  name = "fa:fa-house Dashboard",
  destinations = ["Term", "Issue", "Activities"],
  includes = ["Profile", "Certificates"],
  isStart = true
)
@Composable
fun Dashboard() {
}

@Visualization(
  destinations = ["x->LoginRegister"],
)
@Composable
fun Profile() {
}

@Visualization(
  destinations = ["CertificateInfo"],
)
@Composable
fun Certificates() {
}

@Visualization(
  destinations = ["RevokeOtp"],
)
@Composable
fun CertificateInfo() {
}
