package com.parsuomash.sample.ui.screens.sample

import androidx.compose.runtime.Composable
import com.parsuomash.voyager_safe_args.annotation.Visualization

@Visualization(
  id = "LoginRegister",
  graph = "Login",
  destinations = ["LoginOTP"],
  isOptional = true,
  isStart = true
)
@Visualization(
  id = "IssueRegister",
  graph = "IssueCertificate",
  destinations = ["x->IssueOTP"],
  isOptional = true
)
@Composable
fun Register() {
}

@Visualization(
  id = "LoginOTP",
  graph = "Login",
  destinations = ["x->Dashboard"],
)
@Visualization(
  id = "IssueOTP",
  graph = "IssueCertificate",
  destinations = ["x->Farashenasa"],
  isOptional = true
)
@Visualization(
  id = "RevokeOTP",
  graph = "RevokeCertificate",
  destinations = ["x->Certificates"]
)
@Composable
fun OTP() {
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
  destinations = ["SignPDF", "Activities"],
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
