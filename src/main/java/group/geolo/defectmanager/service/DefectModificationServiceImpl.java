package group.geolo.defectmanager.service;

import group.geolo.defectmanager.entity.Defect;
import group.geolo.defectmanager.entity.DefectModification;
import group.geolo.defectmanager.entity.DefectModificationTable;
import group.geolo.defectmanager.entity.UserInfo;
import group.geolo.defectmanager.repository.DefectModificationRepository;
import group.geolo.defectmanager.repository.DefectRepository;
import group.geolo.defectmanager.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 桀骜(Geolo)
 * @version 1.0
 * @date 2019/10/12
 */
@Service
public class DefectModificationServiceImpl implements DefectModificationService {

    @Autowired
    private DefectModificationRepository defectModificationRepository;
    @Autowired
    private DefectRepository defectRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private HttpServletRequest request;

    private Comparator<DefectModification> modifyTimeDescComparator =
            new Comparator<DefectModification>() {
                @Override
                public int compare(DefectModification o1, DefectModification o2) {
                    return -(int) (o1.getModifyTime().getTime() - o2.getModifyTime().getTime());
                }
            };

    private Comparator<DefectModificationTable> tableModifyTimeDescComparator =
            new Comparator<DefectModificationTable>() {
                @Override
                public int compare(DefectModificationTable o1, DefectModificationTable o2) {
                    return -(int) (o1.getModifyTime().getTime() - o2.getModifyTime().getTime());
                }
            };

    @Override
    public DefectModification getDefectModification(Integer id) {
        return defectModificationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("DefectModification of id = " + id + "is not found."));
    }

    @Override
    public List<DefectModification> getDefectModificationsOfDefect(Integer defectId) {
        return defectModificationRepository.getDefectModificationsByDefectId(defectId)
                .stream()
                .sorted(modifyTimeDescComparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<DefectModificationTable> getDefectModificationTablesOfDefect(Integer defectId) {
        List<DefectModification> modifications = getDefectModificationsOfDefect(defectId);
        List<DefectModificationTable> modificationTables = new ArrayList<>();
        for (int i = 0; i < modifications.size(); i++) {
            String username = userInfoRepository.
                    findById(modifications.get(i).getModifyUserId()).orElse(new UserInfo()).getNickname();
            DefectModificationTable defectModificationTable = new DefectModificationTable(modifications.get(i), username);
            modificationTables.add(defectModificationTable);
        }
        return modificationTables;
    }

    @Override
    public void addDefectModification(DefectModification defectModification) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        defectModification.setModifyUserId(userId);
        defectModification.setModifyTime(new Date());
        defectModificationRepository.save(defectModification);
        Defect defect = defectRepository.findById(defectModification.getDefectId()).orElseThrow(() ->
                new EntityNotFoundException("the defect of modification is not found."));
        defect.setDefectState(defectModification.getResultState());
        defectRepository.save(defect);
    }
}
