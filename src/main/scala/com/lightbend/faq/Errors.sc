object Errors {

  case class Data(email: String, phone: String)


 def validateEmail(email: String): Either[List[String], String] = email match {
   case "hungai@lightbend.com" => Right("Correct email Welcome")
   case _ => Left(List("Bad email"))
 }

  def validatePhone(phone: String): Either[List[String],String] = phone match {
    case "254724" => Right("Correct phone Welcome")
    case _ => Left(List("Bad phone"))
  }

  // It would be nice to have all of these errors be reported simultaneously.
  // A for-comprehension is fail-fast.
  // If some of the evaluations in the for block fails for some reason, the yield statement will not complete.

  def validateData(data: Data): Either[List[String], Data] =
    for  {
      validEmail <- validateEmail(data.email)
      validPhone <- validatePhone(data.phone)
    } yield Data(validEmail,validPhone)


  validateData(Data("h","0"))


  def validateData2(data: Data): Either[List[String],Data] = {
    val validEmail = validateEmail(data.email)
    val validPhone = validatePhone(data.phone)

    (validEmail, validPhone) match {
      case (Right(e),Right(p)) => Right(Data(e,p))
      case (Left(errE), Left(errP)) => Left(errE ++ errP)
      case (Left(errE), _) => Left(errE)
      case (_, Left(errP)) => Left(errP)
    }
  }

  validateData2(Data("h","0"))

}