package com.parsuomash.voyager_safe_args_processor.model

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueArgument

internal inline fun <reified T> Sequence<KSAnnotation>.getValue(
  annotationName: String,
  argumentName: String
): T? = withName(annotationName)
  ?.arguments
  ?.withName(argumentName)
  ?.value as? T

@JvmName("getValueAsString")
internal fun Sequence<KSAnnotation>.getValue(
  annotationName: String,
  argumentName: String
): String? = withName(annotationName)
  ?.arguments
  ?.withName(argumentName)
  ?.value
  ?.toString()

private fun Sequence<KSAnnotation>.withName(annotationName: String): KSAnnotation? =
  firstOrNull { it.shortName.getShortName() == annotationName }

private fun List<KSValueArgument>.withName(argumentName: String): KSValueArgument? =
  firstOrNull { it.name?.getShortName() == argumentName }
