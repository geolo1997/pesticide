package pers.geolo.pesticide.server.repository;

import pers.geolo.pesticide.server.entity.DefectModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 桀骜(Geolo)
 * @version 1.0
 * @date 2019/10/12
 */
@Repository
public interface DefectModificationRepository extends JpaRepository<DefectModification, Integer> {

    List<DefectModification> getDefectModificationsByDefectId(Integer defectId);

//    @Query("select new DefectModificationTable(d, u) from DefectModification d, " +
//            "UserInfo u where ?1 = d.defectId and d.modifyUserId = u.id")
//    List<DefectModificationTable> getDefectModificationTablesByDefectId( Integer defectId);
}
