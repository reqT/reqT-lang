package reqt

import reqt.showDeprecations.old.migration

class TestMetaModel extends munit.FunSuite:
  import meta.*
  import showDeprecations.*

  test("All meta model names consistent with concepts"):
    val names1 = 
      (entityConcepts ++ strAttrConcepts ++ intAttrConcepts ++ relationConcepts).map((name, descr) => name).sorted
    val names2 = concepts.map(_.name).sorted
    assert((names2 diff names1).length == 0, s"missing in meta.concepts: ${(names2 diff names1).mkString(",")}") 
    assert((names1 diff names2).length == 0, s"missing in definitions: ${(names1 diff names2).mkString(",")}") 


  test("All deprecations have consistent advice      "):

    def check(act: String, tpe: String)(xs: Seq[String]) =
      val mig: Seq[String] =
        migration.collect{case ((d, c, n), descr) if d == act && c == tpe => n}.toSeq.sorted
      val ss = xs.sorted
      
      assert((mig diff ss).isEmpty, 
        s"$act $tpe extra in migration advice ${(mig diff ss).mkString(",")}")

      assert((ss diff mig).isEmpty, 
        s"$act $tpe missing in migration advice ${(ss diff mig).mkString(",")}")
      
    check("Deleted", "Entity"   )(deletedEntities)
    check("Added",   "Entity"   )(addedEntities)
    check("Deleted", "Attribute")(deletedAttributes)
    check("Added",   "Attribute")(addedAttributes)
    check("Deleted", "Relation")(deletedRelations)
    check("Added",   "Relation")(addedRelations)
    

