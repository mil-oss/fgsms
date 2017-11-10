<%@page import="org.miloss.fgsms.auxsrv.AuxConstants"%><%@page import="java.util.GregorianCalendar"%><%@page import="javax.xml.datatype.DatatypeFactory"%><%@page import="org.quartz.JobDetail"%><%@page import="org.quartz.impl.StdSchedulerFactory"%><%@page import="org.quartz.Scheduler"%><%@page import="javax.xml.bind.JAXBContext"%><%@page import="javax.xml.bind.Marshaller"%><%@page import="org.miloss.fgsms.auxsrv.status.*"%><%    
    
    
    try {
        org.miloss.fgsms.auxsrv.status.ObjectFactory fac = new org.miloss.fgsms.auxsrv.status.ObjectFactory();
        QuartzStatus qs = fac.createQuartzStatus();
        
        response.setContentType("text/xml");
        
        Scheduler sc = new StdSchedulerFactory().getScheduler(AuxConstants.QUARTZ_SCHEDULER_NAME);
        qs.setSchedulerName(sc.getSchedulerName());
        qs.setSchedulerInstanceId(sc.getSchedulerInstanceId());
        qs.setGroupNames(fac.createGroupNames());
        
        
        
        String[] groups = sc.getJobGroupNames();
        for (int i = 0; i < groups.length; i++) {
            Group g = fac.createGroup();
            g.setName(groups[i]);
            qs.getGroupNames().getGroup().add(g);
        }
        
        
        qs.setJobs(fac.createJobs());
        
        String[] jobNames = sc.getJobNames(AuxConstants.QUARTZ_GROUP_NAME);
        for (int i = 0; i < jobNames.length; i++) {
            Job j = fac.createJob();
            j.setName(jobNames[i]);
            
            JobDetail jd = sc.getJobDetail(jobNames[i], AuxConstants.QUARTZ_GROUP_NAME);
            if (jd == null) {
                //    out.write("Unable to get reference");
            } else {
                j.setDurable(jd.isDurable());
                j.setStateful(jd.isStateful());
                j.setVolatile(jd.isVolatile());
                j.setGroup(jd.getGroup());
                qs.getJobs().getJob().add(j);
            }
            
        }
        qs.setVersion(sc.getMetaData().getVersion());
        qs.setJobsExecuted(sc.getMetaData().getNumberOfJobsExecuted());
        DatatypeFactory df = DatatypeFactory.newInstance();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(sc.getMetaData().getRunningSince());
        qs.setRunningsince((gcal));
        
        
        qs.setTriggers(fac.createTriggers());
        
        
        groups = sc.getTriggerGroupNames();
        for (int i = 0; i < groups.length; i++) {
            TriggerGroup tg = fac.createTriggerGroup();
            tg.setName(groups[i]);
            
            String[] names = sc.getTriggerNames(groups[i]);
            for (int k = 0; k < names.length; k++) {
                Trigger t = fac.createTrigger();
                t.setName(names[k]);
                tg.getTrigger().add(t);
            }
            qs.getTriggers().getTriggerGroup().add(tg);
        }
        
        
        JAXBContext ctx = JAXBContext.newInstance(org.miloss.fgsms.auxsrv.status.Group.class, org.miloss.fgsms.auxsrv.status.GroupNames.class, org.miloss.fgsms.auxsrv.status.Job.class, org.miloss.fgsms.auxsrv.status.Jobs.class, org.miloss.fgsms.auxsrv.status.QuartzStatus.class, org.miloss.fgsms.auxsrv.status.Trigger.class, org.miloss.fgsms.auxsrv.status.TriggerGroup.class, org.miloss.fgsms.auxsrv.status.Triggers.class);
        Marshaller m = ctx.createMarshaller();
        m.marshal(qs, out);
    } catch (Exception ex) {
        response.setStatus(500);
        out.write("Quartz may not be running! " + ex.getMessage());
    }
      
    
%>