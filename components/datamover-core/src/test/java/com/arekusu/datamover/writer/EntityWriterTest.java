package com.arekusu.datamover.writer;

import com.arekusu.datamover.dao.EntityDAO;
import com.arekusu.datamover.dao.SimpleDBDAO;
import com.arekusu.datamover.model.Entity;
import com.arekusu.datamover.test.util.EntityBuilder;
import com.arekusu.datamover.test.util.FieldBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.mockito.Mockito.*;

public class EntityWriterTest {

    DBEntityWriter writer;

    EntityDAO dao;

    @Before
    public void init(){
        writer = new DBEntityWriter();
        dao = mock(SimpleDBDAO.class);
        writer.setDao(dao);
    }

    @Test
    public void simpleWriteTest() {
        Entity linkedEntity = new EntityBuilder()
                .withAlias("LinkedEntity")
                .withSourceField("Src")
                .withDestinationField("Dest")
                .withField(new FieldBuilder().withAlias("Src").withColumn("SrcColumn").withValue("SrcValue").build())
                .build();

        Entity refEntity = new EntityBuilder()
                .withAlias("RefEntity")
                .withSourceField("Src")
                .withDestinationField("Dest")
                .withField(new FieldBuilder().withAlias("Dest").withColumn("DestColumn").withValue("DestValue").build())
                .build();

        Entity entity = new EntityBuilder()
                .withAlias("TestEntity")
                .withSchema("TestSchema")
                .withTable("TestTable")
                .withField(new FieldBuilder().withAlias("Src").withColumn("TestColumn1").withValue("TestValue1").build())
                .withField(new FieldBuilder().withAlias("TestAlias2").withColumn("TestColumn2").withValue("TestValue2").build())
                .withLinkedEntity(linkedEntity)
                .withReferencedEntity(refEntity)
                .build();

        Entity reference = new EntityBuilder(entity).build();

        writer.write(Arrays.asList(entity), null);

        ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);
        verify(dao, times(3)).insertSimpleEntity(entityCaptor.capture());
        verifyNoMoreInteractions(dao);
        assertThat(entityCaptor.getAllValues().get(0), sameBeanAs(linkedEntity));
        assertThat(entityCaptor.getAllValues().get(1), sameBeanAs(reference));
        assertThat(entityCaptor.getAllValues().get(2), sameBeanAs(refEntity));
    }
}
